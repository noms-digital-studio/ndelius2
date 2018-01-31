package services;

import interfaces.OffenderApi;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class FakeOffenderApi implements OffenderApi {
    @Override
    public CompletionStage<String> logon(String username) {
        return CompletableFuture.completedFuture("fake-bearer");
    }

    @Override
    public CompletionStage<Boolean> canAccess(String bearerToken, long offenderId) {
        return CompletableFuture.completedFuture(false);
    }
}
