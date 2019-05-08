package controllers;

import com.google.common.collect.ImmutableList;
import data.CourtDefendant;
import data.Defendant;
import data.DefendantMatchConfidence;
import data.MatchedDefendants;
import helpers.JsonHelper;
import interfaces.OffenderSearch;
import lombok.val;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static controllers.SessionKeys.OFFENDER_API_BEARER_TOKEN;
import static helpers.JwtHelper.principal;

public class CourtDefendantMatchController extends Controller {

    private final views.html.courtMatchResultsTester results;
    private final OffenderSearch offenderSearch;
    private final Form<Defendant> defendantForm;
    private final HttpExecutionContext ec;



    @Inject
    public CourtDefendantMatchController(
            HttpExecutionContext ec,
            FormFactory formFactory,
            views.html.courtMatchResultsTester results,
            OffenderSearch offenderSearch) {
        this.ec = ec;
        this.results = results;
        this.offenderSearch = offenderSearch;
        defendantForm = formFactory.form(Defendant.class);
    }

    public CompletionStage<Result> index() {
        return Optional.ofNullable(session(OFFENDER_API_BEARER_TOKEN))
                .map(bearerToken -> CompletableFuture.completedFuture(ok(results.render(defendantForm.bindFromRequest(), new MatchedDefendants(DefendantMatchConfidence.NONE, ImmutableList.of())))))
                .orElseGet(() ->CompletableFuture.completedFuture(Results.unauthorized()));

    }

    public CompletionStage<Result> search() {
        val defendant = defendantForm.bindFromRequest().get();
        val offender = new CourtDefendant(defendant.getPncNumber(), defendant.getSurname(), defendant.getFirstName(), defendant.getDateOfBirth());

        return Optional.ofNullable(session(OFFENDER_API_BEARER_TOKEN)).map(bearerToken -> {

            Logger.info("AUDIT:{}: Search performed with offender '{}'", principal(bearerToken), offender);
            return offenderSearch.findMatch(bearerToken, offender).
                    thenApplyAsync(matches -> ok(results.render(defendantForm.bindFromRequest(), MatchedDefendants.of(matches))), ec.current());


        }).orElseGet(() -> {

            Logger.warn("Unauthorized search attempted for defendant '{}'. No Offender API bearer token found in session", offender);
            return CompletableFuture.completedFuture(Results.unauthorized());
        });

    }


    public CompletionStage<Result> findDefendantMatch() {
        val offender = Json.fromJson(request().body().asJson(), CourtDefendant.class);

        return Optional.ofNullable(session(OFFENDER_API_BEARER_TOKEN)).map(bearerToken -> {

            Logger.info("AUDIT:{}: Search performed with offender '{}'", principal(bearerToken), offender);
            return offenderSearch.findMatch(bearerToken, offender).
                                thenApply(JsonHelper::okJson);


        }).orElseGet(() -> {

            Logger.warn("Unauthorized findMatch attempted for findMatch term '{}'. No Offender API bearer token found in session", offender);
            return CompletableFuture.completedFuture(Results.unauthorized());
        });
    }


}
