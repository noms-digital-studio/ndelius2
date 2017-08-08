package interfaces;

import java.util.Map;

public interface AnalyticsStore {

    void recordEvent(Map<String, Object> data);
}
