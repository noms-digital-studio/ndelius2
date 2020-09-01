package services;

import com.typesafe.config.Config;
import helpers.JsonHelper;
import interfaces.HealthCheckResult;
import interfaces.UserAwareApiToken;
import lombok.val;
import play.Logger;
import play.libs.ws.WSAuthScheme;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static interfaces.HealthCheckResult.healthy;
import static interfaces.HealthCheckResult.unhealthy;
import static play.mvc.Http.Status.OK;

public class UserAwareAuthenticationApi implements UserAwareApiToken {
    private final String apiBaseUrl;
    private final String username;
    private final String password;
    private final WSClient wsClient;


    @Inject
    public UserAwareAuthenticationApi(Config configuration, WSClient wsClient) {
        this.wsClient = wsClient;
        apiBaseUrl = configuration.getString("nomis.api.url");
        username = configuration.getString("custody.api.auth.username");
        password = configuration.getString("custody.api.auth.password");
    }

    @Override
    public CompletionStage<String> get(String currentUsername) {
        return wsClient.url(apiBaseUrl + "auth/oauth/token")
                .addQueryParameter("username", currentUsername)
                .setContentType("application/x-www-form-urlencoded")
                .setAuth(username, password, WSAuthScheme.BASIC)
                .post("grant_type=client_credentials")
                .thenApply(this::assertOkResponse)
                .thenApply(WSResponse::asJson)
                .thenApply(JsonHelper::jsonToMap)
                .thenApply(map -> map.get("access_token"));
    }

    @Override
    public CompletionStage<HealthCheckResult> isHealthy() {
        return wsClient.url(apiBaseUrl + "auth/health").
                get().
                thenApply(wsResponse -> {

                    if (wsResponse.getStatus() == OK) {
                        return healthy(wsResponse.asJson());
                    }
                    Logger.warn("HMPPS Authentication API Response Status: " + wsResponse.getStatus());
                    return unhealthy(String.format("Status %d", wsResponse.getStatus()));
                }).
                exceptionally(throwable -> {

                    Logger.error("Error while checking HMPPS Authentication API connectivity", throwable);
                    return unhealthy(throwable.getLocalizedMessage());
                });
    }

    private WSResponse assertOkResponse(WSResponse response) {
        if (response.getStatus() != OK) {
            val message = String
                    .format("Unable to call %sauth/oauth/token Status = %d", apiBaseUrl, response.getStatus());
            Logger.error(message);
            throw new RuntimeException(message);
        }
        return response;
    }

}
