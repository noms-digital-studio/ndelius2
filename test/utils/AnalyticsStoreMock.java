package utils;

import interfaces.AnalyticsStore;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AnalyticsStoreMock extends AnalyticsStore {

    default void recordEvent(Map<String, Object> data) {
    }

    default CompletableFuture<List<Map<String, Object>>> recentEvents(int limit) {
        return null;
    }

    default CompletableFuture<List<Map<String, Object>>> sessionEvents(String sessionId) {return null; };

    default CompletableFuture<Map<Integer, Integer>> pageVisits() {
        return null;
    }
}