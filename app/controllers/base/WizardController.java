package controllers.base;

import com.google.common.base.Strings;
import com.typesafe.config.Config;
import data.base.WizardData;
import helpers.Encryption;
import interfaces.AnalyticsStore;
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

import static helpers.FluentHelper.content;

public abstract class WizardController<T extends WizardData> extends Controller {

    private final Environment environment;
    private final AnalyticsStore analyticsStore;
    private final Function1<String, String> viewEncrypter;
    private final List<String> encryptedFields;

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

        return initialParams().thenApplyAsync(params ->

            ok(formRenderer(viewPageName(Integer.parseInt(params.get("pageNumber")))).apply(wizardForm.bind(params))),

        ec.current());
    }

    public final CompletionStage<Result> wizardPost() {

        val boundForm = wizardForm.bindFromRequest();
        val thisPage = boundForm.value().map(WizardData::getPageNumber).orElse(1);

        if (boundForm.hasErrors()) {

            val errorPage = boundForm.allErrors().stream().map(ValidationError::key).findFirst().
                    flatMap(field -> boundForm.value().flatMap(wizardData -> wizardData.getField(field))).
                    map(WizardData::fieldPage).orElse(thisPage);

            val errorData = new HashMap<String, String>(boundForm.rawData());
            errorData.put("pageNumber", errorPage.toString());

            return CompletableFuture.supplyAsync(() -> badRequest(renderPage(errorPage, wizardForm.bind(errorData))), ec.current());

        } else {

            val wizardData = boundForm.get();
            val nextPage = nextPage(wizardData);

            if (nextPage < 1 || nextPage > wizardData.totalPages()) {
                renderingData(wizardData);
            } else {
                wizardData.setPageNumber(nextPage); // Only store real page values as is persisted to Alfresco for re-edit
            }

            return nextPage <= wizardData.totalPages() ?
                    nextPage > 0 ?
                            CompletableFuture.supplyAsync(() -> ok(renderPage(nextPage, wizardForm.fill(wizardData))), ec.current()) :
                            cancelledWizard(wizardData) :
                    completedWizard(wizardData);
        }
    }

    public final CompletionStage<Result> feedbackPost() {

        return CompletableFuture.supplyAsync(() ->
                ok(formRenderer(baseViewName() + "Feedback").apply(wizardForm.bindFromRequest())), ec.current());
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

        return badRequest(formRenderer(viewPageName(wizardData.totalPages())).apply(wizardForm.fill(wizardData)));
    }

    protected void renderingData(T wizardData) {

        if (Strings.isNullOrEmpty(session("id"))) {
            session("id", UUID.randomUUID().toString());
        }

        val feedback = wizardData.getFeedback();
        val eventData = new HashMap<String, Object>()
        {
            {
                put("sessionId", session("id"));
                put("pageNumber", wizardData.getPageNumber());
                put("dateTime", DateTime.now().toDate());
                put("feedback", feedback);
            }
        };

        Logger.info("Session: " + eventData.get("sessionId") + " - Page: " + eventData.get("pageNumber") + " - " + eventData.get("dateTime"));

        if (!Strings.isNullOrEmpty(feedback)) {
            Logger.info("Feedback: " + feedback);
        }

        analyticsStore.recordEvent(eventData);
    }

    protected Function<Form<T>, Content> formRenderer(String viewName) {

        val render = getRenderMethod(viewName, Form.class, Function1.class, WebJarsUtil.class);

        return form -> {

            renderingData(form.value().orElseGet(this::newWizardData));

            return render.map(method -> invokeContentMethod(method, form, viewEncrypter, webJarsUtil)).
                    orElse(Txt.apply("Form Renderer Error"));
        };
    }

    protected T newWizardData() {

        try {
            return wizardForm.getBackedType().newInstance();

        } catch (InstantiationException | IllegalAccessException ex) {

            return null;
        }
    }

    private String viewPageName(int pageNumber) {

        return baseViewName() + pageNumber;
    }

    private Content renderPage(int pageNumber, Form<T> formContent) {

        return formRenderer(viewPageName(pageNumber)).apply(formContent);
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

            return Optional.empty();
        }
    }

    private Content invokeContentMethod(Method method, Object... args) {

        try {
            return (Content) method.invoke(null, args);
        }
        catch (IllegalAccessException | InvocationTargetException ex) {

            return content(ex);
        }
    }
}
