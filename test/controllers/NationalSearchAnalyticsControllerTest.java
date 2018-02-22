package controllers;

import com.fasterxml.jackson.databind.JsonNode;
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
import play.libs.Json;
import play.mvc.Http;
import play.test.WithApplication;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
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
        when(analyticsStore.pageVisits(eq("search-index"), any())).thenReturn(CompletableFuture.completedFuture(0L));
        when(analyticsStore.pageVisits(eq("search-request"), any())).thenReturn(CompletableFuture.completedFuture(0L));
        when(analyticsStore.uniquePageVisits(eq("search-index"), any())).thenReturn(CompletableFuture.completedFuture(0L));
        when(analyticsStore.rankGrouping(eq("search-offender-details"), any())).thenReturn(CompletableFuture.completedFuture(ImmutableMap.of()));
        when(analyticsStore.eventOutcome(eq("search-index"), any())).thenReturn(CompletableFuture.completedFuture(ImmutableMap.of()));
        when(analyticsStore.durationBetween(eq("search-request"), eq("search-offender-details"), any(), eq(60L))).thenReturn(CompletableFuture.completedFuture(ImmutableMap.of(
                1L, 10L,
                2L, 5L,
                3L, 1L
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
    public void returnsOkResponse() {
        val request = new Http.RequestBuilder().method(GET).uri("/nationalSearch/analytics/visitCounts");
        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void returnsUniqueUserVisits() {
        when(analyticsStore.uniquePageVisits(eq("search-index"), any())).thenReturn(CompletableFuture.completedFuture(13L));

        val result = route(app, new Http.RequestBuilder().method(GET).uri("/nationalSearch/analytics/visitCounts"));

        final JsonNode body = Json.parse(contentAsString(result));
        assertThat(body.get("uniqueUserVisits").asLong()).isEqualTo(13);
    }

    @Test
    public void returnsAllVisits() {
        when(analyticsStore.pageVisits(eq("search-index"), any())).thenReturn(CompletableFuture.completedFuture(14L));

        val result = route(app, new Http.RequestBuilder().method(GET).uri("/nationalSearch/analytics/visitCounts"));

        final JsonNode body = Json.parse(contentAsString(result));
        assertThat(body.get("allVisits").asLong()).isEqualTo(14);
    }

    @Test
    public void returnsAllSearches() {
        when(analyticsStore.pageVisits(eq("search-request"), any())).thenReturn(CompletableFuture.completedFuture(15L));

        val result = route(app, new Http.RequestBuilder().method(GET).uri("/nationalSearch/analytics/visitCounts"));

        final JsonNode body = Json.parse(contentAsString(result));
        assertThat(body.get("allSearches").asLong()).isEqualTo(15);
    }

    @Test
    public void returnsRankGrouping() {
        when(analyticsStore.rankGrouping(eq("search-offender-details"), any())).thenReturn(CompletableFuture.completedFuture(ImmutableMap.of(
                1, 1000L,
                2, 200L,
                3, 30L
        )));

        val result = route(app, new Http.RequestBuilder().method(GET).uri("/nationalSearch/analytics/visitCounts"));

        final JsonNode body = Json.parse(contentAsString(result));
        assertThat(body.get("rankGrouping").get("1").asLong()).isEqualTo(1000L);
        assertThat(body.get("rankGrouping").get("2").asLong()).isEqualTo(200L);
        assertThat(body.get("rankGrouping").get("3").asLong()).isEqualTo(30L);
    }

    @Test
    public void returnsEventOutcome() {
        when(analyticsStore.eventOutcome(eq("search-index"), any())).thenReturn(CompletableFuture.completedFuture(ImmutableMap.of(
                "search-index", 1L,
                "search-request", 10L,
                "search-offender-details", 100L,
                "search-legacy-search", 4L
        )));

        val result = route(app, new Http.RequestBuilder().method(GET).uri("/nationalSearch/analytics/visitCounts"));

        final JsonNode body = Json.parse(contentAsString(result));
        assertThat(body.get("eventOutcome").get("search-index").asLong()).isEqualTo(1L);
        assertThat(body.get("eventOutcome").get("search-request").asLong()).isEqualTo(10L);
        assertThat(body.get("eventOutcome").get("search-offender-details").asLong()).isEqualTo(100L);
        assertThat(body.get("eventOutcome").get("search-legacy-search").asLong()).isEqualTo(4L);
    }

    @Test
    public void returnsDurationBetweenStartEndSearch() {
        when(analyticsStore.durationBetween(eq("search-request"), eq("search-offender-details"), any(), eq(60L))).thenReturn(CompletableFuture.completedFuture(ImmutableMap.of(
                1L, 10L,
                2L, 5L,
                3L, 1L
        )));

        val result = route(app, new Http.RequestBuilder().method(GET).uri("/nationalSearch/analytics/visitCounts"));

        final JsonNode body = Json.parse(contentAsString(result));
        assertThat(body.get("durationBetweenStartEndSearch").get("1").asLong()).isEqualTo(10L);
        assertThat(body.get("durationBetweenStartEndSearch").get("2").asLong()).isEqualTo(5L);
        assertThat(body.get("durationBetweenStartEndSearch").get("3").asLong()).isEqualTo(1L);
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