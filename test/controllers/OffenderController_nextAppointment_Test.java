package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableList;
import com.mongodb.rx.client.MongoClient;
import helpers.JsonHelper;
import helpers.JwtHelperTest;
import interfaces.AnalyticsStore;
import interfaces.OffenderApi;
import interfaces.PrisonerApi;
import lombok.val;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

@RunWith(MockitoJUnitRunner.class)
public class OffenderController_nextAppointment_Test extends WithApplication implements ResourceLoader {
    @Mock
    private OffenderApi offenderApi;

    @Before
    public void setUp() {
        when(offenderApi.getOffenderFutureAppointmentsByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(loadJsonResource("/deliusoffender/offenderAppointments.json")));
    }
    @Test
    public void nextAppointmentReturnedForOffenderInSession() {
        when(offenderApi.getOffenderFutureAppointmentsByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(loadJsonResource("/deliusoffender/offenderAppointments.json")));

        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/nextAppointment");

        val result = route(app, request);
        val content = Helpers.contentAsString(result);

        assertThat(result.status()).isEqualTo(OK);
        assertThat(content).contains("\"description\":\"AP PA - Accommodation\"");
    }

    @Test
    public void noNextAppointmentReturns404() {
        when(offenderApi.getOffenderFutureAppointmentsByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(Json.parse("[]")));

        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/nextAppointment");

        val result = route(app, request);

        assertThat(result.status()).isEqualTo(NOT_FOUND);
    }

    @Test
    public void earliestNextAppointmentReturned() {
        val appointments = ImmutableList.of(
                anAppointment(1, "2019-01-19", "13:28:00"),
                anAppointment(2, "2019-01-18", "13:28:00"),
                anAppointment(3, "2019-01-18", "13:27:59"),
                anAppointment(4, "2019-01-18", null),
                anAppointment(5, "2019-01-19", "01:28:00")
                );
        when(offenderApi.getOffenderFutureAppointmentsByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(Json.toJson(appointments)));

        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/nextAppointment");

        val result = route(app, request);
        val content = Helpers.contentAsString(result);

        assertThat(result.status()).isEqualTo(OK);
        assertThat(content).contains("\"appointmentId\":3");
    }


    @Test
    public void nextAppointmentRetrievedUsingOffenderValueInSession() {
        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/nextAppointment");

        route(app, request);

        verify(offenderApi).getOffenderFutureAppointmentsByOffenderId(JwtHelperTest.generateToken(), "123");
    }

    @Test
    public void badRequestWhenOffenderNotInSession() {
        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .method(GET)
                .uri("/offender/nextAppointment");

        val result = route(app, request);

        assertThat(result.status()).isEqualTo(BAD_REQUEST);
    }

    private Map<String, Object> anAppointment(int id, String date, String time) {
        val appointment = JsonHelper.jsonToObjectMap(ArrayNode.class.cast(loadJsonResource("/deliusoffender/offenderAppointments.json")).get(0));
        appointment.replace("appointmentId", id);
        appointment.replace("appointmentDate", date);
        if (time == null) {
            appointment.remove("appointmentStartTime");
        } else {
            appointment.replace("appointmentStartTime", time);
        }
        return appointment;
    }

    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder().
                overrides(
                        bind(OffenderApi.class).toInstance(offenderApi),
                        bind(PrisonerApi.class).toInstance(mock(PrisonerApi.class)),
                        bind(AnalyticsStore.class).toInstance(mock(AnalyticsStore.class)),
                        bind(RestHighLevelClient.class).toInstance(mock(RestHighLevelClient.class)),
                        bind(MongoClient.class).toInstance(mock(MongoClient.class))
                )
                .configure("params.user.token.valid.duration", "1h")
                .build();
    }

}