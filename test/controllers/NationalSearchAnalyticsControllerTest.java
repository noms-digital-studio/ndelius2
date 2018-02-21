package controllers;

import com.google.common.collect.ImmutableMap;
import interfaces.AnalyticsStore;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.WithApplication;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class NationalSearchAnalyticsControllerTest extends WithApplication {
    @Mock
    private AnalyticsStore analyticsStore;
    @Captor
    private ArgumentCaptor<LocalDateTime> fromCaptor;


    @Before
    public void setup() {
        when(analyticsStore.pageVisits(eq("search-index"), any())).thenReturn(CompletableFuture.completedFuture(100L));
        when(analyticsStore.pageVisits(eq("search-request"), any())).thenReturn(CompletableFuture.completedFuture(1000L));
        when(analyticsStore.uniquePageVisits(eq("search-index"), any())).thenReturn(CompletableFuture.completedFuture(10L));
        when(analyticsStore.rankGrouping(eq("search-offender-details"), any())).thenReturn(CompletableFuture.completedFuture(ImmutableMap.of(
                1, 1000L,
                2, 200L,
                3, 30L
        )));
        when(analyticsStore.eventOutcome(eq("search-index"), any())).thenReturn(CompletableFuture.completedFuture(ImmutableMap.of(
                "search-index", 1L,
                "search-request", 10L,
                "search-offender-details", 100L,
                "search-legacy-search", 4L
        )));
    }
    @Test
    public void defaultsToBeginningOf2017WhenFromIsMissing() {
        val request = new Http.RequestBuilder().method(GET).uri("/nationalSearch/analytics/visitCounts");
        route(app, request);

        verify(analyticsStore).pageVisits(eq("search-index"), fromCaptor.capture());
        assertThat(fromCaptor.getValue()).isEqualTo("2017-01-01T00:00:00");

    }

    @Test
    public void usesFromParameterWhenPresent() {
        val request = new Http.RequestBuilder().method(GET).uri("/nationalSearch/analytics/visitCounts?from=2018-02-03T08:29:59Z");
        route(app, request);

        verify(analyticsStore).pageVisits(eq("search-index"), fromCaptor.capture());
        assertThat(fromCaptor.getValue()).isEqualTo("2018-02-03T08:29:59");

    }


    @Test
    public void returnsOkResponseWithCountsAsJson() {
        val request = new Http.RequestBuilder().method(GET).uri("/nationalSearch/analytics/visitCounts");
        val result = route(app, request);

        assertEquals(OK, result.status());
        assertEquals("{\"uniqueUserVisits\":10,\"allVisits\":100,\"allSearches\":1000,\"rankGrouping\":{\"1\":1000,\"2\":200,\"3\":30},\"eventOutcome\":{\"search-index\":1,\"search-request\":10,\"search-offender-details\":100,\"search-legacy-search\":4}}", contentAsString(result));
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().
                overrides(
                        bind(AnalyticsStore.class).toInstance(analyticsStore)
                )
                .build();
    }

}