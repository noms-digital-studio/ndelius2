package bdd.wiremock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import helpers.JsonHelper;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import play.Environment;
import play.Mode;
import play.libs.Json;

import javax.inject.Inject;
import javax.inject.Named;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static scala.io.Source.fromInputStream;
import static utils.InstitutionalReportHelpers.anInstitutionalReport;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static utils.CourtAppearanceHelpers.aCourtReport;
import static utils.CourtAppearanceHelpers.someCourtAppearances;
import static utils.OffenceHelpers.someOffences;
import static utils.OffenderHelper.aFemaleOffenderWithNoContactDetails;
import static utils.OffenderHelper.anOffenderWithNoContactDetails;

public class OffenderApiMock {
    @Inject
    @Named("offenderApiWireMock")
    private WireMockServer offenderApiWireMock;

    public OffenderApiMock start() {
            offenderApiWireMock.start();
        return this;
    }

    public OffenderApiMock stop() {
        offenderApiWireMock.stop();
        return this;
    }

    public OffenderApiMock stubDefaults() {
        offenderApiWireMock.stubFor(
                post(urlEqualTo("/documentLink")).willReturn(created()));

        offenderApiWireMock.stubFor(
                post(urlEqualTo("/logon")).willReturn(ok().withBody("aBearerToken")));

        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/crn/.*/all"))
                        .willReturn(ok().withBody(JsonHelper.stringify(anOffenderWithNoContactDetails()))));

        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/crn/.*/courtReports/.*"))
                        .willReturn(ok().withBody(JsonHelper.stringify(aCourtReport()))));

        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/crn/.*/offences"))
                        .willReturn(ok().withBody(JsonHelper.stringify(someOffences().getItems()))));

        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/crn/.*/courtAppearances"))
                        .willReturn(ok().withBody(JsonHelper.stringify(someCourtAppearances().getItems()))));


        offenderApiWireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X12345/all"))
                    .willReturn(ok().withBody(JsonHelper.stringify(anOffenderWithNoContactDetails()))));

        offenderApiWireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X54321/all"))
                    .willReturn(ok().withBody(JsonHelper.stringify(aFemaleOffenderWithNoContactDetails()))));

        offenderApiWireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X12345/institutionalReports/12345"))
                    .willReturn(ok().withBody(JsonHelper.stringify(anInstitutionalReport()))));

        offenderApiWireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X54321/institutionalReports/54332"))
                    .willReturn(ok().withBody(JsonHelper.stringify(anInstitutionalReport()))));

        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/offenderId/.*/all"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/deliusoffender/offender.json"))));

        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/offenderId/.*/userAccess"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/deliusoffender/userAccess.json"))));


        return this;
    }

    public OffenderApiMock stubOffenderWithName(String fullName) {
        val firstName = fullName.split(" ")[0];
        val surname = fullName.split(" ")[1];
        offenderApiWireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X12345/all"))
                        .willReturn(ok().withBody(JsonHelper.stringify(
                                anOffenderWithNoContactDetails()
                                        .toBuilder()
                                        .firstName(firstName)
                                        .surname(surname)
                                        .middleNames(ImmutableList.of())
                                        .build()
                        ))));

        return this;
    }

    public OffenderApiMock stubOffenderWithNameAndNoNomsNumber(String fullName) {
        val firstName = fullName.split(" ")[0];
        val surname = fullName.split(" ")[1];
        offenderApiWireMock.stubFor(
                get(urlEqualTo("/offenders/crn/X12345/all"))
                        .willReturn(ok().withBody(JsonHelper.stringify(
                                anOffenderWithNoContactDetails()
                                        .toBuilder()
                                        .firstName(firstName)
                                        .surname(surname)
                                        .middleNames(ImmutableList.of())
                                        .otherIds(ImmutableMap.of())
                                        .build()
                        ))));

        return this;
    }

    public OffenderApiMock stubOffenderWithDetails(Map<String, String> offenderDetailsMap) {
        val offender = ObjectNode.class.cast(Json.parse(loadResource("/deliusoffender/offender.json")));

        offenderDetailsMap.forEach((key, value) -> {
            val keysSplit = Arrays.asList(key.split("\\."));
            val branches = keysSplit.subList(0, keysSplit.size() - 1);
            val leaf = keysSplit.get(keysSplit.size() - 1);
            final ObjectNode[] context = {offender};
            branches.forEach(subKey -> {
                context[0] = ObjectNode.class.cast(context[0].get(subKey));
            });

            if (StringUtils.isBlank(value)) {
                context[0].remove(leaf);
            } else {
                context[0].put(leaf, value);
            }
        });

        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/offenderId/.*/all"))
                        .willReturn(
                                okForContentType("application/json", Json.stringify(offender))));

        return this;
    }

    public OffenderApiMock stubOffenderWithResource(String resource) {
        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/offenderId/.*/all"))
                        .willReturn(
                                okForContentType("application/json", loadResource(resource))));


        return this;
    }

    private static String loadResource(String resource) {
        return fromInputStream(new Environment(Mode.TEST).resourceAsStream(resource), "UTF-8").mkString();
    }

}
