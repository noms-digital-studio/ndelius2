package services;

import com.typesafe.config.Config;
import helpers.JsonHelper;
import interfaces.HealthCheckResult;
import interfaces.PrisonerApiToken;
import lombok.val;
import play.Logger;
import play.cache.AsyncCacheApi;
import play.libs.ws.WSAuthScheme;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static interfaces.HealthCheckResult.healthy;
import static interfaces.HealthCheckResult.unhealthy;
import static play.mvc.Http.Status.OK;

public class NomisCustodyAuthenticationApi implements PrisonerApiToken {
    private final AsyncCacheApi cache;
    private final int cacheTime;
    private final String apiBaseUrl;
    private final String username;
    private final String password;
    private final WSClient wsClient;
    private final static String TOKEN_KEY = "custody.api.token";


    @Inject
    public NomisCustodyAuthenticationApi(Config configuration, WSClient wsClient, AsyncCacheApi cache) {
        this.wsClient = wsClient;
        apiBaseUrl = configuration.getString("hmpps.auth.url");
        cacheTime = configuration.getInt("hmpps.auth.token.cache.time.seconds");
        username =  configuration.getString("hmpps.auth.username");
        password =  configuration.getString("hmpps.auth.password");
        this.cache = cache;
    }

    @Override
    public String get() {
        throw new RuntimeException("Use getAsync");
    }

    @Override
    public CompletionStage<String> getAsync() {
        return cache.getOrElseUpdate(TOKEN_KEY, () -> lookupNewToken(), cacheTime);
    }

    @Override
    public CompletionStage<HealthCheckResult> isHealthy() {

        return wsClient.url(apiBaseUrl + "auth/health").
                get().
                thenApply(wsResponse -> {

                    if (wsResponse.getStatus() == OK) {
                        return healthy(wsResponse.asJson());
                    }
                    Logger.warn("NOMIS Authentication API Response Status: " + wsResponse.getStatus());
                    return unhealthy(String.format("Status %d", wsResponse.getStatus()));
                }).
                exceptionally(throwable -> {

                    Logger.error("Error while checking NOMIS Authentication API connectivity", throwable);
                    return unhealthy(throwable.getLocalizedMessage());
                });
    }

    @Override
    public void clearToken() {
        cache.remove(TOKEN_KEY);
    }


    private CompletionStage<String> lookupNewToken() {
        return wsClient.url(apiBaseUrl + "auth/oauth/token")
                .setContentType("application/x-www-form-urlencoded")
                .setAuth(username, password, WSAuthScheme.BASIC)
                .post("grant_type=client_credentials")
                .thenApply(this::assertOkResponse)
                .thenApply(WSResponse::asJson)
                .thenApply(JsonHelper::jsonToMap)
                .thenApply(map -> map.get("access_token"));

    }

    private WSResponse assertOkResponse(WSResponse response) {
        if (response.getStatus() != OK) {
            val message = String.format("Unable to call %sauth/oauth/token Status = %d",  apiBaseUrl, response.getStatus());
            Logger.error(message);
            throw new RuntimeException(message);
        }
        return response;
    }

}
