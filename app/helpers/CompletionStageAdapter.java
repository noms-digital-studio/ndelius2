package helpers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class CompletionStageAdapter<T> {

    protected final CompletableFuture<T> promise = new CompletableFuture<>();

    public CompletionStage<T> stage() {

        return promise;
    }
}
