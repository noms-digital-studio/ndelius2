package controllers.base;

import com.typesafe.config.Config;
import data.base.WizardData;
import helpers.Encryption;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.val;
import org.webjars.play.WebJarsUtil;
import play.Environment;
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
    private final Form<T> wizardForm;
    private final String baseViewName;
    private final Function1<String, String> viewEncrypter;
    private final List<String> encryptedFields;

    protected final WebJarsUtil webJarsUtil;
    protected final Function<String, String> encrypter;
    protected final Function<String, String> decrypter;
    protected final HttpExecutionContext ec;

    protected WizardController(HttpExecutionContext ec,
                               WebJarsUtil webJarsUtil,
                               Config configuration,
                               Environment environment,
                               EncryptedFormFactory formFactory,
                               Class<T> wizardType,
                               String baseViewName) {

        this.ec = ec;
        this.webJarsUtil = webJarsUtil;
        this.environment = environment;
        this.baseViewName = baseViewName;

        wizardForm = formFactory.form(wizardType, this::decryptParams);
        encryptedFields = newWizardData().encryptedFields().map(Field::getName).collect(Collectors.toList());

        val paramsSecretKey = configuration.getString("params.secret.key");

        encrypter = plainText -> Encryption.encrypt(plainText, paramsSecretKey);
        decrypter = encrypted -> Encryption.decrypt(encrypted, paramsSecretKey);

        viewEncrypter = new FromJavaFunction(encrypter); // Use Scala functions in the view.scala.html markup
    }

    public final CompletionStage<Result> wizardGet() {

        return initialParams().thenApply(params -> {

            params.putIfAbsent("pageNumber", "0");

            return ok(formRenderer(viewPageName(1)).apply(wizardForm.bind(params)));
        });
    }

    public final CompletionStage<Result> wizardPost() {

        val boundForm = wizardForm.bindFromRequest();
        val thisPage = boundForm.value().map(WizardData::getPageNumber).orElse(1);

        final Function<Integer, Content> renderPage = pageNumber -> formRenderer(viewPageName(pageNumber)).apply(boundForm);

        if (boundForm.hasErrors()) {

            val errorPage = boundForm.allErrors().stream().map(ValidationError::key).findFirst().
                    flatMap(field -> boundForm.value().flatMap(wizardData -> wizardData.getField(field))).
                    map(WizardData::fieldPage).orElse(thisPage);

            return CompletableFuture.supplyAsync(() -> badRequest(renderPage.apply(errorPage)), ec.current());

        } else {

            val wizardData = boundForm.get();
            val nextPage = nextPage(wizardData);

            return nextPage <= wizardData.totalPages() ?
                    CompletableFuture.supplyAsync(() -> ok(renderPage.apply(nextPage)), ec.current()) :
                    completedWizard(wizardData);
        }
    }

    protected CompletionStage<Map<String, String>> initialParams() { // Overridable in derived Controllers to supplant initial params

        val params = request().queryString().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));

        return CompletableFuture.supplyAsync(() -> decryptParams(params), ec.current());
    }

    protected Map<String, String> modifyParams(Map<String, String> params) {

        return params;
    }

    protected Integer nextPage(T wizardData) {  // Overridable in derived Controllers jump pages based on content

        return Optional.ofNullable(wizardData.getJumpNumber()).orElse(wizardData.getPageNumber() + 1);
    }

    protected abstract CompletionStage<Result> completedWizard(T wizardData);

    protected final Result wizardFailed(T wizardData) {

        return badRequest(formRenderer(viewPageName(wizardData.totalPages())).apply(wizardForm.fill(wizardData)));
    }

    private String viewPageName(int pageNumber) {

        return baseViewName + pageNumber;
    }

    private Map<String, String> decryptParams(Map<String, String> params) {

        modifyParams(params).keySet().stream().filter(encryptedFields::contains).forEach(field ->
                params.put(field, decrypter.apply(params.get(field)))
        );

        return params;
    }

    private Function<Form<T>, Content> formRenderer(String viewName) {

        val render = getRenderMethod(viewName, Form.class, Function1.class, WebJarsUtil.class);

        return form -> {

            val content = render.map(method -> invokeContentMethod(method, form, viewEncrypter, webJarsUtil));

            return content.orElse(Txt.apply("Form Renderer Error"));
        };
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

    private T newWizardData() {

        try {
            return wizardForm.getBackedType().newInstance();

        } catch (InstantiationException | IllegalAccessException ex) {

            return null;
        }
    }
}
