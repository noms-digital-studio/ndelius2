package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import helpers.Encryption;
import helpers.JsonHelper;
import helpers.JwtHelper;
import interfaces.OffenderApi;
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
import static play.mvc.Results.badRequest;

public class OffenderController extends Controller {

    private final PrisonerApi prisonerApi;
    private final OffenderApi offenderApi;
    private final HttpExecutionContext ec;
    private final Function<String, String> decrypter;
    private final Supplier<Result> noPhotoResult;
    private final Function<String, String> encrypter;


    @Inject
    public OffenderController(Config configuration, PrisonerApi prisonerApi, OffenderApi offenderApi, HttpExecutionContext ec, Environment environment) {
        this.ec = ec;
        this.prisonerApi = prisonerApi;
        this.offenderApi = offenderApi;

        val paramsSecretKey = configuration.getString("params.secret.key");

        encrypter = plainText -> Encryption.encrypt(plainText, paramsSecretKey).orElseThrow(() -> new RuntimeException("Encrypt failed"));
        decrypter = encrypted -> Encryption.decrypt(encrypted, paramsSecretKey).orElseThrow(() -> new RuntimeException("Decrypt failed"));
        noPhotoResult = () -> Optional.ofNullable(noPhotoImage(environment)).map(Results::ok).orElseGet(Results::badRequest);
    }

    public static String generateOneTimeImageReference(Function<String, String> encrypter, String nomisId, String bearerToken) {
        return encrypter.apply(JsonHelper.stringify(ImmutableMap.of(
                "user", JwtHelper.principal(bearerToken),
                "noms", nomisId,
                "tick", Instant.now().toEpochMilli()
        )));
    }

    public CompletionStage<Result> image(String oneTimeNomisRef) { // Can only be used by the user that generated a search result just now

        val reference = JsonHelper.jsonToMap(decrypter.apply(oneTimeNomisRef)); // Encrypted so cannot be changed from generation in oneTimeNomisRef()
                                                                                // The associated Offender has already been checked canAccess just now
        val validUser = Optional.ofNullable(reference.get("user")).
                map(user -> user.equals(principal(session(SessionKeys.OFFENDER_API_BEARER_TOKEN)))).     //@TODO: Shared constant
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

                    Logger.warn("Failed to get Nomis Image due to {}", throwable.getMessage());
                    return noPhotoResult.get();
                });
    }

    public CompletionStage<Result> detail() {
        val bearerToken = session(SessionKeys.OFFENDER_API_BEARER_TOKEN);
        return Optional.ofNullable(session(SessionKeys.OFFENDER_ID)).
                map(offenderId ->
                        offenderApi.getOffenderDetailByOffenderId(bearerToken, offenderId).
                                thenApply(jsonNode -> addImageRef(jsonNode, bearerToken)).
                                thenApply(JsonHelper::okJson)).
                orElse(CompletableFuture.completedFuture(badRequest("no offender found in session"))).
                thenApply(result -> result.withHeader(CACHE_CONTROL, "no-cache, no-store, must-revalidate")).
                thenApply(result -> result.withHeader(PRAGMA, "no-cache")).
                thenApply(result -> result.withHeader(EXPIRES, "0"));
    }

    private ObjectNode addImageRef(JsonNode jsonNode, String bearerToken) {
        val offender = ObjectNode.class.cast(jsonNode);

        Optional.ofNullable(offender.get("otherIds").get("nomsNumber")).ifPresent(nomsNumberNode -> {
            offender.put("oneTimeNomisRef", OffenderController.generateOneTimeImageReference(encrypter, nomsNumberNode.asText(), bearerToken));
        });


        return offender;
    }
}
