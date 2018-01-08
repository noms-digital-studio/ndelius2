package utils;

import java.util.concurrent.CompletableFuture;

public class SimpleAnalyticsStoreMock implements AnalyticsStoreMock {
    @Override
    public CompletableFuture<Boolean> isUp() {
        throw new RuntimeException("Not yet implemented");
    }
}
