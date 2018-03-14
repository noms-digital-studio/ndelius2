package services.stubs;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import interfaces.OffenderApi;
import play.libs.Json;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.libs.Json.toJson;

public class StubOffenderApi implements OffenderApi {
    @Override
    public CompletionStage<String> logon(String username) {
        // JWT Header/Body is {"alg":"HS512"}{"sub":"cn=fake.user,cn=Users,dc=moj,dc=com","uid":"fake.user","exp":1517631939}
        return CompletableFuture.completedFuture("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjbj1mYWtlLnVzZXIsY249VXNlcnMsZGM9bW9qLGRjPWNvbSIsInVpZCI6ImZha2UudXNlciIsImV4cCI6MTUxNzYzMTkzOX0=.FsI0VbLbqLRUGo7GXDEr0hHLvDRJjMQWcuEJCCaevXY1KAyJ_05I8V6wE6UqH7gB1Nq2Y4tY7-GgZN824dEOqQ");
    }

    @Override
    public CompletionStage<Boolean> canAccess(String bearerToken, long offenderId) {
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletionStage<Boolean> isHealthy() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public CompletionStage<JsonNode> searchDb(Map<String, String> queryParams) {
        return CompletableFuture.completedFuture(toJson(ImmutableMap.of("db", "example")));
    }

    @Override
    public CompletionStage<JsonNode> searchLdap(Map<String, String> queryParams) {
        return CompletableFuture.completedFuture(Json.toJson(ImmutableMap.of("ldap", "example")));
    }
}
