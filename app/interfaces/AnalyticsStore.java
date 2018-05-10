package interfaces;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AnalyticsStore {

    void recordEvent(Map<String, Object> data);

    CompletableFuture<List<Map<String, Object>>> recentEvents(int limit);

    CompletableFuture<Map<Integer, Long>> pageVisits();

    CompletableFuture<Long> pageVisits(String eventType, LocalDateTime from);

    CompletableFuture<Long> uniquePageVisits(String eventType, LocalDateTime from);

    CompletableFuture<Map<Integer, Long>> rankGrouping(String eventType, LocalDateTime from);

    CompletableFuture<Map<String, Long>> eventOutcome(String eventType, LocalDateTime from);

    CompletableFuture<Map<Long, Long>> durationBetween(String firstEventType, String secondEventType, LocalDateTime from, long groupBySeconds);

    CompletableFuture<Map<String, Long>> countGroupingArray(String eventType, String countFieldName, LocalDateTime from);

    CompletableFuture<Boolean> isUp();

    CompletableFuture<List<Map<String, Object>>> nationalSearchFeedback();

    CompletableFuture<List<Map<String, Object>>> sfpsrFeedback();

    CompletableFuture<Map<String, Integer>> filterCounts(LocalDateTime localDateTime);

    CompletableFuture<Map<String, Object>> weeklySatisfactionScores();
}
