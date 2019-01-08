package bdd.wiremock;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import helpers.JsonHelper;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import play.Environment;
import play.Mode;
import play.libs.Json;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static scala.io.Source.fromInputStream;
import static utils.CourtAppearanceHelpers.aCourtReport;
import static utils.CourtAppearanceHelpers.someCourtAppearances;
import static utils.InstitutionalReportHelpers.anInstitutionalReport;
import static utils.OffenceHelpers.someOffences;
import static utils.OffenderHelper.aFemaleOffenderWithNoContactDetails;
import static utils.OffenderHelper.anOffenderWithNoContactDetails;

public class OffenderApiMock {
    @Inject
    @Named("offenderApiWireMock")
    private WireMockServer offenderApiWireMock;

    @Data
    @Builder
    public static class Registration {
        private String register;
        private String type;
        private String riskColour;
        private LocalDate startDate;

    }
    @Data
    @Builder
    public static class Conviction {
        private LocalDate referralDate;
        private String mainOffenceDescription;
        private String latestCourtAppearanceDescription;
        private boolean active;
        private Sentence sentence;
    }

    @Data
    @Builder
    public static class Sentence {
        private String description;
        private int length;
        private String lengthUnit;

    }
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

        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/offenderId/.*/registrations"))
                        .willReturn(
                                okForContentType("application/json", loadResource("/deliusoffender/offenderRegistrations.json"))));

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
                                okForContentType("application/json", loadResource("/deliusoffender/" + resource))));


        return this;
    }

    public OffenderApiMock stubOffenderWithRegistrations(List<Registration> registrations) {
        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/offenderId/.*/registrations"))
                        .willReturn(
                                okForContentType("application/json", JsonHelper.stringify(
                                        registrations
                                        .stream()
                                        .map(registration -> {
                                            val template = JsonHelper.jsonToObjectMap(loadResource("/deliusoffender/offenderRegistration.json"));
                                            template.replace("register", ImmutableMap.of("code", registration.getRegister().toUpperCase(), "description", registration.getRegister()));
                                            template.replace("type", ImmutableMap.of("code", registration.getType().toUpperCase(), "description", registration.getType()));
                                            template.replace("startDate", registration.getStartDate().format(DateTimeFormatter.ISO_DATE));
                                            template.replace("riskColour", registration.getRiskColour());

                                            return template;
                                        })
                                        .collect(Collectors.toList()))
                                )));

        return this;
    }
    public OffenderApiMock stubOffenderWithConvictions(List<Conviction> convictions) {
        offenderApiWireMock.stubFor(
                get(urlMatching("/offenders/offenderId/.*/convictions"))
                        .willReturn(
                                okForContentType("application/json", JsonHelper.stringify(
                                        convictions
                                        .stream()
                                        .map(conviction -> {
                                            val template = JsonHelper.jsonToObjectMap(loadResource("/deliusoffender/offenderConviction.json"));
                                            template.replace("referralDate", conviction.getReferralDate().format(DateTimeFormatter.ISO_DATE));
                                            val latestCourtAppearanceOutcome = (Map<String, Object>)template.get("latestCourtAppearanceOutcome");
                                            latestCourtAppearanceOutcome.replace("description", conviction.getLatestCourtAppearanceDescription());
                                            val offences = (List<Map<String, Object>>) template.get("offences");
                                            val offenceDetail = (Map<String, Object>)offences.get(0).get("detail");
                                            offenceDetail.replace("description", conviction.getMainOffenceDescription());

                                            if (conviction.getSentence() == null) {
                                                template.remove("sentence");
                                            } else {
                                                val sentence = (Map<String, Object>)template.get("sentence");
                                                sentence.replace("description", conviction.getSentence().getDescription());
                                                sentence.replace("originalLength", conviction.getSentence().getLength());
                                                sentence.replace("originalLengthUnits", conviction.getSentence().getLengthUnit());
                                            }

                                            template.replace("active", conviction.isActive());


                                            return template;
                                        })
                                        .collect(Collectors.toList()))
                                )));

        return this;
    }


    private static String loadResource(String resource) {
        return fromInputStream(new Environment(Mode.TEST).resourceAsStream(resource), "UTF-8").mkString();
    }

}
