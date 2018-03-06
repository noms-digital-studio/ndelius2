package services.fakes;

import lombok.val;
import org.junit.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;


public class InMemoryAnalyticsStoreTest {

    @Test
    public void returnCorrectPageVisitCounts() {
        val inMemoryAnalyticsStore = new InMemoryAnalyticsStore();
        inMemoryAnalyticsStore.recordEvent(anEventForPageNumber(3));
        inMemoryAnalyticsStore.recordEvent(anEventForPageNumber(3));
        inMemoryAnalyticsStore.recordEvent(anEventForPageNumber(3));

        inMemoryAnalyticsStore.recordEvent(anEventForPageNumber(5));
        inMemoryAnalyticsStore.recordEvent(anEventForPageNumber(5));
        val visits = inMemoryAnalyticsStore.pageVisits().join();

        assertThat(visits.get(3)).isEqualTo(3);
        assertThat(visits.get(5)).isEqualTo(2);
    }

    private HashMap<String, Object> anEventForPageNumber(int pageNumber) {
        val event = new HashMap<String, Object>();
        event.put("pageNumber", pageNumber);
        return event;
    }


}