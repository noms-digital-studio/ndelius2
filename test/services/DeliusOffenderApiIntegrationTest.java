package services;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.google.common.collect.ImmutableList;
import interfaces.OffenderApi;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import play.Application;
import play.Environment;
import play.Mode;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

import java.util.AbstractMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okForContentType;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static scala.io.Source.fromInputStream;

public class DeliusOffenderApiIntegrationTest extends WithApplication {
    private OffenderApi offenderApi;
    private static final int PORT = 18080;

    @Rule
    public WireMockRule wireMock = new WireMockRule(PORT);


    @Before
    public void beforeEach() {
        val n01 = loadResource("/deliusoffender/probationAreaByCode_N01.json");
        val n02 = loadResource("/deliusoffender/probationAreaByCode_N02.json");
        val offender = loadResource("/deliusoffender/offender.json");
        val courtAppearances = loadResource("/deliusoffender/courtAppearances.json");

        wireMock.stubFor(
                get(urlEqualTo("/probationAreas/code/N01"))
                        .willReturn(
                                okForContentType("application/json",  n01)));

        wireMock.stubFor(
                get(urlEqualTo("/probationAreas/code/N02"))
                        .willReturn(
                                okForContentType("application/json",  n02)));

        wireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X12345/all"))
                        .willReturn(
                                okForContentType("application/json",  offender)));

        wireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X12345/courtAppearances"))
                        .willReturn(
                                okForContentType("application/json",  courtAppearances)));

        offenderApi = instanceOf(OffenderApi.class);
    }


    @Test
    public void singleProbationAreaCodeMakesSingleAPICall() {
        offenderApi.probationAreaDescriptions("ABC", ImmutableList.of("N01")).toCompletableFuture().join();

        wireMock.verify(getRequestedFor(urlEqualTo("/probationAreas/code/N01")));
    }

    @Test
    public void multipleProbationAreaCodesMakesMultipleAPICall() {
        offenderApi.probationAreaDescriptions("ABC", ImmutableList.of("N01", "N02")).toCompletableFuture().join();

        wireMock.verify(getRequestedFor(urlEqualTo("/probationAreas/code/N01")));
        wireMock.verify(getRequestedFor(urlEqualTo("/probationAreas/code/N02")));
    }

    @Test
    public void descriptionsAreCached() {
        offenderApi.probationAreaDescriptions("ABC", ImmutableList.of("N01")).toCompletableFuture().join();

        wireMock.verify(1, getRequestedFor(urlEqualTo("/probationAreas/code/N01")));

        wireMock.resetRequests();

        offenderApi.probationAreaDescriptions("ABC", ImmutableList.of("N01", "N02")).toCompletableFuture().join();
        wireMock.verify(0, getRequestedFor(urlEqualTo("/probationAreas/code/N01")));
        wireMock.verify(1, getRequestedFor(urlEqualTo("/probationAreas/code/N02")));

        wireMock.resetRequests();

        val probationAreaCodeToDescriptionMap = offenderApi.probationAreaDescriptions("ABC", ImmutableList.of("N01", "N02")).toCompletableFuture().join();

        wireMock.verify(0, getRequestedFor(anyUrl()) );

        assertThat(probationAreaCodeToDescriptionMap)
                .contains(entry("N01", "NPS North West"))
                .contains(entry("N02", "NPS North East"));

    }

    @Test
    public void doesNotBotherCallingAPIWhenEmptyListSupplied() {
        offenderApi.probationAreaDescriptions("ABC", ImmutableList.of()).toCompletableFuture().join();

        wireMock.verify(0, getRequestedFor(anyUrl()) );
    }

    @Test
    public void setsBearerTokenInHeader() {
        offenderApi.probationAreaDescriptions("ABC", ImmutableList.of("N01")).toCompletableFuture().join();

        wireMock.verify(getRequestedFor(anyUrl()).withHeader("Authorization", new EqualToPattern("Bearer ABC")) );
    }


    @Test
    public void returnsMapOfCodeDescriptions() {
        val probationAreaCodeToDescriptionMap = offenderApi.probationAreaDescriptions("ABC", ImmutableList.of("N01", "N02")).toCompletableFuture().join();

        assertThat(probationAreaCodeToDescriptionMap)
                .contains(entry("N01", "NPS North West"))
                .contains(entry("N02", "NPS North East"));
    }

    @Test
    public void getsOffenderByCrn() {
        val offender = offenderApi.getOffenderByCrn("ABC", "X12345").toCompletableFuture().join();

        assertThat(offender.getFirstName()).isEqualTo("John");
        assertThat(offender.getSurname()).isEqualTo("Smith");
        assertThat(offender.getOtherIds()).extracting("pncNumber").contains("2018/123456P");
        assertThat(offender.getContactDetails().mainAddress().get().getStatus().getCode()).isEqualTo("M");
        wireMock.verify(getRequestedFor(urlEqualTo("/offenders/crn/X12345/all")));
    }

    @Test
    public void getsCourtAppearancesByCrn() {
        val courtAppearances = offenderApi.getCourtAppearancesByCrn("ABC", "X12345").toCompletableFuture().join();

        assertThat(courtAppearances.getItems().size()).isEqualTo(3);
        assertThat(courtAppearances.getItems().get(0).getCourtAppearanceId()).isEqualTo(1);
        assertThat(courtAppearances.getItems().get(0).getCourt().getCourtId()).isEqualTo(1);
        assertThat(courtAppearances.getItems().get(0).getCourtReports().get(0).getCourtReportId()).isEqualTo(1);
        wireMock.verify(getRequestedFor(urlEqualTo("/offenders/crn/X12345/courtAppearances")));
    }

    private static Map.Entry<String, String> entry(String code, String description) {
        return new AbstractMap.SimpleEntry<>(code, description);
    }

    private static String loadResource(String resource) {
        return fromInputStream(new Environment(Mode.TEST).resourceAsStream(resource), "UTF-8").mkString();
    }


    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder()
                .configure("offender.api.url", String.format("http://localhost:%d/", PORT))
                .build();
    }


}
