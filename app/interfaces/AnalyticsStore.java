package interfaces;

import org.bson.Document;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AnalyticsStore {

    void recordEvent(Map<String, Object> data);

    CompletableFuture<List<Map<String, Object>>> recentEvents(int limit);

    CompletableFuture<Map<Integer, Integer>> pageVisits();
}
