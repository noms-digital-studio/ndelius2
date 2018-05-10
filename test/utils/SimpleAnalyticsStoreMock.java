package utils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SimpleAnalyticsStoreMock implements AnalyticsStoreMock {
    @Override
    public CompletableFuture<Boolean> isUp() {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public CompletableFuture<Map<String, Object>> weeklySatisfactionScores() {
        throw new RuntimeException("Not yet implemented");
    }
}
