package controllers;

import com.google.common.base.Strings;
import com.typesafe.config.Config;
import helpers.Encryption;
import helpers.JsonHelper;
import interfaces.PrisonerApi;
import lombok.val;
import play.Environment;
import play.Logger;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

import static helpers.JwtHelper.principal;
import static helpers.StaticImage.noPhotoImage;

public class OffenderController extends Controller {

    private final PrisonerApi prisonerApi;
    private final HttpExecutionContext ec;
    private final Function<String, String> decrypter;
    private final Supplier<Result> noPhotoResult;

    @Inject
    public OffenderController(Config configuration, PrisonerApi prisonerApi, HttpExecutionContext ec, Environment environment) {

        this.ec = ec;
        this.prisonerApi = prisonerApi;

        val paramsSecretKey = configuration.getString("params.secret.key");

        decrypter = encrypted -> Encryption.decrypt(encrypted, paramsSecretKey);
        noPhotoResult = () -> Optional.ofNullable(noPhotoImage(environment)).map(Results::ok).orElseGet(Results::badRequest);
    }

    public CompletionStage<Result> image(String oneTimeNomisRef) { // Can only be used by the user that generated a search result just now

        val reference = JsonHelper.jsonToMap(decrypter.apply(oneTimeNomisRef)); // Encrypted so cannot be changed from generation in oneTimeNomisRef()
                                                                                // The associated Offender has already been checked canAccess just now
        val validUser = Optional.ofNullable(reference.get("user")).
                map(user -> user.equals(principal(session("offenderApiBearerToken")))).     //@TODO: Shared constant
                orElse(false);
                                                                            // oneTimeNomisRef() creation in ElasticOffenderSearch results
        val validTick = Optional.ofNullable(reference.get("tick")).         // is synchronous and fast so does not affect Search performance.
                map(Long::valueOf).                                         // The slow asynchronous fetching of Nomis Images is orchestrated
                map(tick -> Instant.now().toEpochMilli() - tick).           // by the ReactJS front end after the Search results have been
                map(Math::abs).                                             // displayed in the browser inherently by setting image.src to
                map(difference -> difference < 60000).                      // the oneTimeNomisRef and displayed to users after results load
                orElse(false);
                                                            // Without the above would need to find associate Offender and call API canAccess again 
        return Optional.ofNullable(reference.get("noms")).
                filter(nomisId -> validUser && validTick && !Strings.isNullOrEmpty(nomisId)).
                map(nomisId -> prisonerApi.getImage(nomisId).thenApplyAsync(bytes -> ok(bytes).as("image/jpeg"), ec.current())).
                orElseGet(() -> {

                    Logger.warn("Invalid OneTimeNomisRef: {}", oneTimeNomisRef);
                    return CompletableFuture.supplyAsync(noPhotoResult);
                }).
                exceptionally(throwable -> {

                    Logger.error("Failed to get Nomis Image", throwable);
                    return noPhotoResult.get();
                });
    }
}
