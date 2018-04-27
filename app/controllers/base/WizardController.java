package controllers.base;

import com.google.common.base.Strings;
import com.typesafe.config.Config;
import data.base.WizardData;
import data.viewModel.PageStatus;
import helpers.Encryption;
import helpers.JsonHelper;
import interfaces.AnalyticsStore;
import lombok.val;
import org.joda.time.DateTime;
import org.webjars.play.WebJarsUtil;
import play.Environment;
import play.Logger;
import play.data.Form;
import play.data.validation.ValidationError;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;
import play.twirl.api.Txt;
import scala.Function1;
import scala.compat.java8.functionConverterImpls.FromJavaFunction;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static helpers.FluentHelper.content;

public abstract class WizardController<T extends WizardData> extends Controller {

    private final AnalyticsStore analyticsStore;
    private final List<String> encryptedFields;
    private final Environment environment;

    protected final Function1<String, String> viewEncrypter;
    protected final Form<T> wizardForm;
    protected final WebJarsUtil webJarsUtil;
    protected final Function<String, String> encrypter;
    protected final Function<String, String> decrypter;
    protected final HttpExecutionContext ec;

    protected WizardController(HttpExecutionContext ec,
                               WebJarsUtil webJarsUtil,
                               Config configuration,
                               Environment environment,
                               AnalyticsStore analyticsStore,
                               EncryptedFormFactory formFactory,
                               Class<T> wizardType) {

        this.ec = ec;
        this.webJarsUtil = webJarsUtil;
        this.environment = environment;
        this.analyticsStore = analyticsStore;

        wizardForm = formFactory.form(wizardType, this::decryptParams);
        encryptedFields = newWizardData().encryptedFields().map(Field::getName).collect(Collectors.toList());

        val paramsSecretKey = configuration.getString("params.secret.key");

        encrypter = plainText -> Encryption.encrypt(plainText, paramsSecretKey);
        decrypter = encrypted -> Encryption.decrypt(encrypted, paramsSecretKey);

        viewEncrypter = new FromJavaFunction(encrypter); // Use Scala functions in the view.scala.html markup
    }

    public final CompletionStage<Result> wizardGet() {

        return initialParams().thenApplyAsync(params -> {

            val errorMessage = params.get("errorMessage");

            if (Strings.isNullOrEmpty(errorMessage)) {

                val boundForm = wizardForm.bind(params);
                val thisPage = boundForm.value().map(WizardData::getPageNumber).orElse(1);
                val pageStatuses = getPageStatuses(boundForm.value(), thisPage, null);

                return ok(renderPage(thisPage, boundForm, pageStatuses));

            } else {

                return badRequest(renderErrorMessage(errorMessage));
            }

        }, ec.current());
    }

    public final CompletionStage<Result> wizardPost() {

        val boundForm = wizardForm.bindFromRequest();
        val thisPage = boundForm.value().map(WizardData::getPageNumber).orElse(1);
        val visitedPages = new StringBuilder();
        val pageStatuses = getPageStatuses(boundForm.value(), thisPage, visitedPages);

        if (boundForm.hasErrors()) {

            val errorPage = boundForm.allErrors().stream().map(ValidationError::key).findFirst().
                    flatMap(field -> boundForm.value().flatMap(wizardData -> wizardData.getField(field))).
                    map(WizardData::fieldPage).orElse(thisPage);

            val errorData = new HashMap<String, String>(boundForm.rawData());
            errorData.put("pageNumber", errorPage.toString());
            errorData.put("visitedPages", visitedPages.toString());

            return CompletableFuture.supplyAsync(() -> {
                Logger.debug("Bad data posted to wizard: " + boundForm.allErrors());
                return badRequest(renderPage(errorPage, wizardForm.bind(errorData), pageStatuses));
            }, ec.current());

        } else {

            val wizardData = boundForm.get();
            val nextPage = nextPage(wizardData);

            wizardData.setVisitedPages(visitedPages.toString());

            if (nextPage < 1 || nextPage > wizardData.totalPages()) {
                renderingData(wizardData);
            } else {
                wizardData.setPageNumber(nextPage); // Only store real page values as is persisted to Alfresco for re-edit
            }

            return nextPage <= wizardData.totalPages() ?
                    nextPage > 0 ?
                            CompletableFuture.supplyAsync(() -> ok(renderPage(nextPage, wizardForm.fill(wizardData), pageStatuses)), ec.current()) :
                            cancelledWizard(wizardData) :
                    completedWizard(wizardData);
        }
    }

    public final CompletionStage<Result> feedbackPost() {

        return CompletableFuture.supplyAsync(() -> {

            val formData = wizardForm.bindFromRequest();

            return ok(formRenderer(baseViewName() + "Feedback").apply(
                    formData, getPageStatuses(formData.value(), 0, null)));

        }, ec.current());
    }

    protected CompletionStage<Map<String, String>> initialParams() { // Overridable in derived Controllers to supplant initial params

        val params = request().queryString().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));

        return CompletableFuture.supplyAsync(() -> decryptParams(params), ec.current());
    }

    protected Map<String, String> modifyParams(Map<String, String> params, Consumer<String> paramEncrypter) {

        return params;
    }

    protected Integer nextPage(T wizardData) {  // Overridable in derived Controllers jump pages based on content

        return Optional.ofNullable(wizardData.getJumpNumber()).orElse(wizardData.getPageNumber() + 1);
    }

    protected abstract String baseViewName();

    protected abstract CompletionStage<Result> completedWizard(T wizardData);

    protected abstract CompletionStage<Result> cancelledWizard(T wizardData);

    protected final Result wizardFailed(T wizardData) {

        val formData = wizardForm.fill(wizardData);

        return badRequest(renderPage(wizardData.totalPages(), formData,
                getPageStatuses(formData.value(), wizardData.getPageNumber(), null)));
    }

    protected void renderingData(T wizardData) {

        if (Strings.isNullOrEmpty(session("id"))) {
            session("id", UUID.randomUUID().toString());
        }

        val feedback = new HashMap<String, Object>()
        {
            {
                put("email", wizardData.getEmail());
                put("rating", wizardData.getRating());
                put("feedback", wizardData.getFeedback());
                put("role", wizardData.getRoleother() == null || wizardData.getRoleother().isEmpty() ? wizardData.getRole() : wizardData.getRoleother());
                put("provider", wizardData.getProvider());
                put("region", wizardData.getRegion());
            }
        };

        val eventData = new HashMap<String, Object>()
        {
            {
                put("username", wizardData.getOnBehalfOfUser());
                put("sessionId", session("id"));
                put("pageNumber", wizardData.getPageNumber());
                put("dateTime", DateTime.now().toDate());
                put("feedback", feedback);
            }
        };

        Logger.info("Session: " + eventData.get("sessionId") + " - Page: " + eventData.get("pageNumber") + " - " + eventData.get("dateTime"));

        analyticsStore.recordEvent(eventData);
    }

    protected BiFunction<Form<T>, Map<Integer, PageStatus>, Content> formRenderer(String viewName) {

        val render = getRenderMethod(viewName, Form.class, Function1.class, Map.class, WebJarsUtil.class, Environment.class);

        return (form, pageStatuses) -> {

            renderingData(form.value().orElseGet(this::newWizardData));

            return render.map(method -> invokeContentMethod(method, form, viewEncrypter, pageStatuses, webJarsUtil, environment)).orElseGet(() -> {

                val errorMessage = new StringBuilder();

                errorMessage.append("Form Renderer Error\n");
                errorMessage.append(viewName);
                errorMessage.append("\n");
                errorMessage.append(form.rawData());

                Logger.error(errorMessage.toString());
                return renderErrorMessage(errorMessage.toString());
            });
        };
    }

    protected Content renderErrorMessage(String errorMessage) {

        return Txt.apply(errorMessage);
    }

    protected T newWizardData() {

        try {
            return wizardForm.getBackedType().newInstance();

        } catch (InstantiationException | IllegalAccessException ex) {

            Logger.error("Unable to instantiate new Wizard Data", ex);
            return null;
        }
    }

    private Map<Integer, PageStatus> getPageStatuses(Optional<T> boundForm, int thisPage, StringBuilder jsonVisitedPages) {

        val errorPages = boundForm.map(value ->
                value.validateAll().stream().map(ValidationError::key).map(value::getField).
                        filter(Optional::isPresent).map(Optional::get).map(WizardData::fieldPage).distinct()
        ).orElseGet(Stream::empty).collect(Collectors.toList());

        val previouslyVisited = (List<Integer>)JsonHelper.readValue(boundForm.map(WizardData::getVisitedPages).
                        flatMap(value -> Strings.isNullOrEmpty(value) ? Optional.empty() : Optional.of(value)).
                        orElse("[]"),
                List.class);

        val visitedPages = Stream.concat(Stream.of(thisPage), previouslyVisited.stream()).distinct().sorted().collect(Collectors.toList());

        if (jsonVisitedPages != null) {
            jsonVisitedPages.append(JsonHelper.stringify(visitedPages));
        }

        return IntStream.rangeClosed(1, newWizardData().totalPages()).boxed().collect(Collectors.toMap(
                Function.identity(), page -> new PageStatus(visitedPages.contains(page), !errorPages.contains(page))));
    }

    private String viewPageName(int pageNumber) {

        return baseViewName() + pageNumber;
    }

    private Content renderPage(int pageNumber, Form<T> formContent, Map<Integer, PageStatus> pageStatuses) {

        return formRenderer(viewPageName(pageNumber)).apply(formContent, pageStatuses);
    }

    private Map<String, String> decryptParams(Map<String, String> params) {

        Consumer<String> paramEncrypter = key -> Optional.ofNullable(params.get(key)).map(value -> params.put(key, encrypter.apply(value)));

        val pageNumber = Optional.ofNullable(params.get("pageNumber")).orElse("");
        val jumpNumber = Optional.ofNullable(params.get("jumpNumber")).orElse("");

        final BiFunction<Map<String, String>, Consumer<String>, Map<String, String>> modifier =         // Don't modify if submission was from the feedback
                pageNumber.equals(jumpNumber) ? (ignored1, ignored2) -> params : this::modifyParams;    // form - only time jump an page are the same

        modifier.apply(params, paramEncrypter).keySet().stream().filter(encryptedFields::contains).forEach(field ->
                params.put(field, decrypter.apply(params.get(field)))
        );


        return params;
    }

    private Optional<Method> getRenderMethod(String viewName, Class<?>... parameterTypes) {

        try {
            return Optional.of(environment.classLoader().loadClass(viewName).getDeclaredMethod("render", parameterTypes));
        }
        catch (ClassNotFoundException | NoSuchMethodException ex) {

            Logger.error("Unable to Render View: " + viewName, ex);
            return Optional.empty();
        }
    }

    private Content invokeContentMethod(Method method, Object... args) {

        try {
            return (Content) method.invoke(null, args);
        }
        catch (IllegalAccessException | InvocationTargetException ex) {

            Logger.error("Unable to Invoke Method: " + method.getName(), ex);
            return content(ex);
        }
    }
}
