package interfaces;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AnalyticsStore {

    void recordEvent(Map<String, Object> data);

    CompletableFuture<List<Map<String, Object>>> recentEvents(int limit);

    CompletableFuture<List<Map<String, Object>>> sessionEvents(String sessionId);

    CompletableFuture<Map<Integer, Integer>> pageVisits();

    CompletableFuture<Boolean> isUp();
}
