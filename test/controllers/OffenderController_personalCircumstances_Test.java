package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.rx.client.MongoClient;
import helpers.JwtHelperTest;
import interfaces.AnalyticsStore;
import interfaces.OffenderApi;
import interfaces.PrisonerApi;
import lombok.Builder;
import lombok.Data;
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

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

@RunWith(MockitoJUnitRunner.class)
public class OffenderController_personalCircumstances_Test extends WithApplication implements ResourceLoader {
    @Mock
    private OffenderApi offenderApi;

    @Before
    public void setUp() {
        when(offenderApi.getOffenderPersonalCircumstancesByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(loadJsonResource("/deliusoffender/offenderPersonalCircumstances.json")));
    }
    @Test
    public void personalCircumstancesReturnedForOffenderInSession() {
        when(offenderApi.getOffenderPersonalCircumstancesByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(loadJsonResource("/deliusoffender/offenderPersonalCircumstances.json")));

        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/personalCircumstances");

        val result = route(app, request);

        assertThat(result.status()).isEqualTo(OK);
    }



    private Long[] idsOf(JsonNode content) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(ArrayNode.class.cast(content).elements(), Spliterator.ORDERED), false)
                .map(node -> node.get("personalCircumstanceId").asLong())
                .collect(Collectors.toList())
                .toArray(new Long[]{});
    }


    @Test
    public void personalCircumstancesReturnedAreFilteredLeavingNoEndDatedOnes() {
        val originalJson = asArrayNode(
                aPersonalCircumstance(PersonalCircumstance
                        .builder()
                        .id(1L)
                        .personalCircumstanceTypeCode("A")
                        .personalCircumstanceTypeDescription("Accommodation")
                        .personalCircumstanceSubTypeCode("A")
                        .personalCircumstanceSubTypeDescription("Moved to private house")
                        .startDate("2019-01-01")
                        .build()),
                aPersonalCircumstance(PersonalCircumstance
                        .builder()
                        .id(2L)
                        .personalCircumstanceTypeCode("A")
                        .personalCircumstanceTypeDescription("Accommodation")
                        .personalCircumstanceSubTypeCode("B")
                        .personalCircumstanceSubTypeDescription("No fixed abode")
                        .startDate("2019-02-01")
                        .build()),
                aPersonalCircumstance(PersonalCircumstance
                        .builder()
                        .id(3L)
                        .personalCircumstanceTypeCode("C")
                        .personalCircumstanceTypeDescription("CPA")
                        .personalCircumstanceSubTypeCode("C")
                        .personalCircumstanceSubTypeDescription("N/A")
                        .startDate("2019-03-01")
                        .endDate("2019-04-01")
                        .build())
        );
        assertThat(originalJson.size()).isEqualTo(3);

        when(offenderApi.getOffenderPersonalCircumstancesByOffenderId(any(), any())).thenReturn(CompletableFuture.completedFuture(originalJson));

        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/personalCircumstances");

        val result = route(app, request);
        val content = Json.parse(Helpers.contentAsString(result));

        assertThat(idsOf(content)).containsExactly(1L, 2L);
    }


    @Test
    public void personalCircumstancesRetrievedUsingOffenderValueInSession() {
        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .session("offenderId", "123")
                .method(GET)
                .uri("/offender/personalCircumstances");

        route(app, request);

        verify(offenderApi).getOffenderPersonalCircumstancesByOffenderId(JwtHelperTest.generateToken(), "123");
    }

    @Test
    public void badRequestWhenOffenderNotInSession() {
        val request = new Http.RequestBuilder()
                .session("offenderApiBearerToken", JwtHelperTest.generateToken())
                .method(GET)
                .uri("/offender/personalCircumstances");

        val result = route(app, request);

        assertThat(result.status()).isEqualTo(BAD_REQUEST);
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

    @Data
    @Builder
    private static class PersonalCircumstance {
        private String personalCircumstanceTypeCode;
        private String personalCircumstanceTypeDescription;
        private String personalCircumstanceSubTypeCode;
        private String personalCircumstanceSubTypeDescription;
        private String startDate;
        private String endDate;
        private long id;

    }

    JsonNode aPersonalCircumstance(PersonalCircumstance personalCircumstance) {
        val template = ObjectNode.class.cast(loadJsonResource("/deliusoffender/offenderPersonalCircumstance.json"));
        template.put("startDate", personalCircumstance.getStartDate());
        template.put("personalCircumstanceId", personalCircumstance.getId());
        if (personalCircumstance.getEndDate() == null) {
            template.remove("endDate");
        } else {
            template.put("endDate", personalCircumstance.getEndDate());
        }

        val personalCircumstanceType = ObjectNode.class.cast(template.get("personalCircumstanceType"));
        personalCircumstanceType.put("description", personalCircumstance.getPersonalCircumstanceTypeDescription());
        personalCircumstanceType.put("code", personalCircumstance.getPersonalCircumstanceTypeCode());
        val personalCircumstanceSubType = ObjectNode.class.cast(template.get("personalCircumstanceSubType"));
        personalCircumstanceSubType.put("description", personalCircumstance.getPersonalCircumstanceSubTypeDescription());
        personalCircumstanceSubType.put("code", personalCircumstance.getPersonalCircumstanceSubTypeCode());


        return template;
    }

    ArrayNode asArrayNode(JsonNode ...nodes) {
        val parent = JsonNodeFactory.instance.arrayNode();
        Stream.of(nodes).forEach(parent::add);
        return parent;
    }
}