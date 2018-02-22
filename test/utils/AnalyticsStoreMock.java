package utils;

import interfaces.AnalyticsStore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AnalyticsStoreMock extends AnalyticsStore {

    default void recordEvent(Map<String, Object> data) {
    }

    default CompletableFuture<List<Map<String, Object>>> recentEvents(int limit) {
        return null;
    }

    default CompletableFuture<Map<Integer, Long>> pageVisits() {
        return null;
    }

    default CompletableFuture<Long> pageVisits(String eventType, LocalDateTime from) {
        return null;
    }

    default CompletableFuture<Long> uniquePageVisits(String eventType, LocalDateTime from) {
        return null;
    }

    default CompletableFuture<Map<Integer, Long>> rankGrouping(String eventType, LocalDateTime from) {
        return null;
    }

    default CompletableFuture<Map<String, Long>> eventOutcome(String eventType, LocalDateTime from) {
        return null;
    }

    default CompletableFuture<Map<Long, Long>> durationBetween(String firstEventType, String secondEventType, LocalDateTime from, long groupBySeconds) {
        return null;
    }

}