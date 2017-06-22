package controllers.base;

import data.annotations.OnPage;
import data.annotations.RequiredOnPage;
import data.base.WizardData;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.val;
import play.Environment;
import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Content;

public abstract class WizardController<T extends WizardData> extends Controller {

    private final HttpExecutionContext ec;
    private final Environment environment;
    private final Form<T> wizardForm;
    private final String baseViewName;

    protected WizardController(HttpExecutionContext ec,
                               Environment environment,
                               FormFactory formFactory,
                               Class<T> wizardType,
                               String baseViewName) {

        this.ec = ec;
        this.environment = environment;
        this.wizardForm = formFactory.form(wizardType);
        this.baseViewName = baseViewName;
    }

    public Result wizardGet() {

        val params = request().queryString().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));

        params.put("pageNumber", "0");

        return ok(formRenderer(baseViewName + 1).apply(wizardForm.bind(params)));
    }

    public CompletionStage<Result> wizardPost() {

        val boundForm = wizardForm.bindFromRequest();
        val thisPage = boundForm.value().map(WizardData::getPageNumber).orElse(1);

        Function<Integer, Content> renderPage = pageNumber -> formRenderer(baseViewName + pageNumber).apply(boundForm);

        if (boundForm.hasErrors()) {

            val errorPage = boundForm.errors().keySet().stream().findFirst().
                    flatMap(field -> boundForm.value().flatMap(wizardData -> wizardData.getField(field))).
                    map(field -> {

                        field.setAccessible(true);

                        return field.isAnnotationPresent(OnPage.class) ?
                                field.getAnnotation(OnPage.class).value() :
                                field.getAnnotation(RequiredOnPage.class).value();

                    }).orElse(thisPage);

            return CompletableFuture.supplyAsync(() -> badRequest(renderPage.apply(errorPage)), ec.current());

        } else {

            val wizardData = boundForm.get();

            if (thisPage < wizardData.totalPages()) {

                return CompletableFuture.supplyAsync(() -> ok(renderPage.apply(nextPage(wizardData))), ec.current());

            } else {

                return completedWizard(wizardData);
            }
        }
    }

    protected Integer nextPage(T wizardData) {

        return Optional.ofNullable(wizardData.getJumpNumber()).orElse(wizardData.getPageNumber() + 1);
    }

    protected abstract CompletionStage<Result> completedWizard(T wizardData);

    private Function<Form<T>, Content> formRenderer(String viewName) {

        try {
            val render = environment.classLoader().loadClass(viewName).getDeclaredMethod("render", Form.class);

            return form -> {
                try {
                    return (Content) render.invoke(null, form);
                }
                catch (IllegalAccessException | InvocationTargetException ex) {
                    return null;
                }
            };
        }
        catch (ClassNotFoundException | NoSuchMethodException ex) {
            return form -> null;
        }
    }
}
