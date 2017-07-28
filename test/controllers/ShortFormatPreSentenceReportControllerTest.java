package controllers;

import com.google.common.collect.ImmutableMap;
import helpers.Encryption;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import lombok.val;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.Helpers;
import play.test.WithApplication;

import static org.junit.Assert.*;
import static play.inject.Bindings.bind;
import static play.mvc.Http.RequestBuilder;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
import static play.api.test.CSRFTokenHelper.addCSRFToken;

public class ShortFormatPreSentenceReportControllerTest extends WithApplication implements PdfGenerator, DocumentStore {

    @Test
    public void getSampleReportOK() {

        val request = new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void getSampleReportConsumesDtoQueryStrings() {

        val request = new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?name=i8%2Fp1Ti7JMS%2FjO%2BPOhHtGA%3D%3D&foobar=xyz987");

        val content = Helpers.contentAsString(route(app, request));

        assertTrue(content.contains("Smith, John"));
        assertFalse(content.contains("xyz987"));
    }

    @Test
    public void getSampleReportWithDocumentIdDecryptsAndRetrievesFromStore() {

        try {

            val secretKey = "ThisIsASecretKey";
            val clearDocumentId = "12345";
            val clearUserName = "John Smith";

            val documentId = URLEncoder.encode(Encryption.encrypt(clearDocumentId, secretKey), "UTF-8");
            val onBehalfOfUser = URLEncoder.encode(Encryption.encrypt(clearUserName, secretKey), "UTF-8");

            val request = new RequestBuilder().method(GET).
                    uri("/report/shortFormatPreSentenceReport?documentId=" + documentId + "&onBehalfOfUser=" + onBehalfOfUser);

            val content = Helpers.contentAsString(route(app, request));

            assertTrue(content.contains(clearDocumentId));          // Decrypted documentId is returned by Mock retrieveOriginalData
            assertTrue(content.contains(clearUserName));            // Decrypted onBehalfOfUser is returned by Mock retrieveOriginalData
            assertTrue(content.contains("Retrieved From Store"));   // Returned from Mock retrieveOriginalData

        } catch (Exception ex) {

            fail(ex.getMessage());
        }
    }

    @Test
    public void postSampleReportPage1TitleOnlyReturnsBadRequest() {

        val formData = ImmutableMap.of(
                "name", "",
                "pageNumber", "1"
        );
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage1AllFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("pageNumber", "1");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage2SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("pageNumber", "2");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage2AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("pageNumber", "2");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage3SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("otherInformationSource", "true");
                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage3WithSpellingMistakeReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("otherInformationSource", "true");
                put("otherInformationDetails", "This texxt has speeling mistakes");
                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage3WithSpellingMistakeAndOverrideReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("otherInformationSource", "true");
                put("otherInformationDetails", "This texxt has speeling mistakes");
                put("ignoreOtherInformationDetailsSpelling", "true");
                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage3AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage4WithSpellingMistakeReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence spelld wrng");
                put("offenceSummary", "Some offence summary spelld wrng");
                put("offenceAnalysis", "Some offence analysis spelld wrng");
                put("pageNumber", "4");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage4WithSpellingMistakeAndOverrideReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence spelld wrng");
                put("ignoreMainOffenceSpelling", "true");
                put("offenceSummary", "Some offence summary spelld wrng");
                put("ignoreOffenceSummarySpelling", "true");
                put("offenceAnalysis", "Some offence analysis spelld wrng");
                put("ignoreOffenceAnalysisSpelling", "true");
                put("pageNumber", "4");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage4SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("ignoreOtherInformationDetailsSpelling", "false");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("pageNumber", "4");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage4AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("ignoreOtherInformationDetailsSpelling", "false");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("pageNumber", "4");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage5WithSpellingMistakeReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment speld wrng");
                put("pageNumber", "5");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage5WithSpellingMistakeAndOverrideReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment speld wrng");
                put("ignoreOffenderAssessmentSpelling", "true");
                put("pageNumber", "5");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage5SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("pageNumber", "5");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage5AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("pageNumber", "5");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage6WithSpellingMistakeReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("ignoreOtherInformationDetailsSpelling", "false");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending speld wrng");
                put("pageNumber", "6");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage6WithSpellingMistakeAndOverrideReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("ignoreOtherInformationDetailsSpelling", "false");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending speld wrng");
                put("ignorePatternOfOffendingSpelling", "true");
                put("pageNumber", "6");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage6AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("ignoreOtherInformationDetailsSpelling", "false");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending");
                put("pageNumber", "6");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage7WithSpellingMistakeReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending");
                put("previousSupervisionResponse", "Some previous supervision response speld wrng");
                put("likelihoodOfReOffending", "Some likelihood of re-offending speld wrng");
                put("pageNumber", "7");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage7WithSpellingMistakeAndOverrideReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending");
                put("previousSupervisionResponse", "Some previous supervision response speld wrng");
                put("ignorePreviousSupervisionResponseSpelling", "true");
                put("likelihoodOfReOffending", "Some likelihood of re-offending speld wrng");
                put("ignoreLikelihoodOfReOffendingSpelling", "true");
                put("pageNumber", "7");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage7SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending");
                put("previousSupervisionResponse", "Some previous supervision response");
                put("pageNumber", "7");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage7AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending");
                put("previousSupervisionResponse", "Some previous supervision response");
                put("likelihoodOfReOffending", "Some likelihood of re-offending");
                put("pageNumber", "7");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage8WithSpellingMistakeReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("ignoreOtherInformationDetailsSpelling", "false");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending");
                put("previousSupervisionResponse", "Some previous supervision response");
                put("likelihoodOfReOffending", "Some likelihood of re-offending");
                put("riskOfSeriousHarm", "An example of a risk assessment speld wrng");
                put("pageNumber", "8");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage8WithSpellingMistakeAndOverrideReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("ignoreOtherInformationDetailsSpelling", "false");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending");
                put("previousSupervisionResponse", "Some previous supervision response");
                put("likelihoodOfReOffending", "Some likelihood of re-offending");
                put("riskOfSeriousHarm", "An example of a risk assessment speld wrng");
                put("ignoreRiskOfSeriousHarmSpelling", "true");
                put("pageNumber", "8");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage8SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("ignoreOtherInformationDetailsSpelling", "false");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending");
                put("previousSupervisionResponse", "Some previous supervision response");
                put("likelihoodOfReOffending", "Some likelihood of re-offending");
                put("pageNumber", "8");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage8AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("ignoreOtherInformationDetailsSpelling", "false");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending");
                put("previousSupervisionResponse", "Some previous supervision response");
                put("likelihoodOfReOffending", "Some likelihood of re-offending");
                put("riskOfSeriousHarm", "An example of a risk assessment");
                put("pageNumber", "8");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage9WithSpellingMistakeReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("ignoreOtherInformationDetailsSpelling", "false");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending");
                put("previousSupervisionResponse", "Some previous supervision response");
                put("likelihoodOfReOffending", "Some likelihood of re-offending");
                put("riskOfSeriousHarm", "An example of a risk assessment");
                put("proposal", "An example of a proposalspeld wring");
                put("pageNumber", "9");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage9WithSpellingMistakeAndOverrideReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("ignoreOtherInformationDetailsSpelling", "false");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending");
                put("previousSupervisionResponse", "Some previous supervision response");
                put("likelihoodOfReOffending", "Some likelihood of re-offending");
                put("riskOfSeriousHarm", "An example of a risk assessment");
                put("ignoreRiskOfSeriousHarmSpelling", "true");
                put("proposal", "An example of a proposal speld wring");
                put("ignoreProposalSpelling", "true");
                put("pageNumber", "9");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage9SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("ignoreOtherInformationDetailsSpelling", "false");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending");
                put("previousSupervisionResponse", "Some previous supervision response");
                put("likelihoodOfReOffending", "Some likelihood of re-offending");
                put("riskOfSeriousHarm", "An example of a risk assessment");
                put("pageNumber", "9");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage9AllRequiredFieldsReturnsOK() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("ignoreOtherInformationDetailsSpelling", "false");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some pattern of offending");
                put("previousSupervisionResponse", "Some previous supervision response");
                put("likelihoodOfReOffending", "Some likelihood of re-offending");
                put("riskOfSeriousHarm", "An example of a risk assessment");
                put("proposal", "An example of a proposal");
                put("pageNumber", "9");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage10SomeFieldsMissingReturnsBadRequest() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some assessment");
                put("previousSupervisionResponse", "Some assessment");
                put("likelihoodOfReOffending", "Some assessment");
                put("riskOfSeriousHarm", "An example of a risk assessment");
                put("proposal", "An example of a proposal");
                put("reportAuthor", "Robert Jenkins");
                put("pageNumber", "10");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage10AllRequiredFieldsReturnsOKAndPdfGenerated() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some assessment");
                put("previousSupervisionResponse", "Some assessment");
                put("likelihoodOfReOffending", "Some assessment");
                put("riskOfSeriousHarm", "An example of a risk assessment");
                put("proposal", "An example of a proposal");
                put("reportAuthor", "Robert Jenkins");
                put("office", "Manchester and Salford Magistrates Court");
                put("pageNumber", "10");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        pdfGenerated = false;

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
        assertTrue(pdfGenerated);
    }

    @Test
    public void postUpdatedReportPage10AllRequiredFieldsReturnsOKAndPdfGenerated() {

        Function<String, String> encrypter = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encrypter.apply("johnsmith"));
                put("entityId", encrypter.apply("12345"));
                put("documentId", encrypter.apply("23456"));
                put("name", encrypter.apply("John Smith"));
                put("dateOfBirth", encrypter.apply("06/02/1976"));
                put("age", encrypter.apply("41"));
                put("address", encrypter.apply("10 High Street"));
                put("crn", encrypter.apply("B56789"));
                put("pnc", encrypter.apply("98793030"));
                put("court", encrypter.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encrypter.apply("01/02/2017"));
                put("localJusticeArea", encrypter.apply("Greater Manchester"));
                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("offenderAssessment", "Some assessment");
                put("patternOfOffending", "Some assessment");
                put("previousSupervisionResponse", "Some assessment");
                put("likelihoodOfReOffending", "Some assessment");
                put("riskOfSeriousHarm", "An example of a risk assessment");
                put("proposal", "An example of a proposal");
                put("reportAuthor", "Robert Jenkins");
                put("office", "Manchester and Salford Magistrates Court");
                put("pageNumber", "10");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");
        pdfGenerated = false;

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
        assertTrue(pdfGenerated);
    }

    private boolean pdfGenerated;

    @Override
    public <T> CompletionStage<Byte[]> generate(String templateName, T values) {

        pdfGenerated = true;

        return CompletableFuture.supplyAsync(() -> new Byte[0]);    // Mocked PdfGenerator returns empty Byte array
    }

    private boolean pdfUploaded;

    @Override
    public CompletionStage<Map<String, String>> uploadNewPdf(Byte[] document, String filename, String onBehalfOfUser, String originalData, String crn, Long entityId) {

        pdfUploaded = true;

        return CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123"));
    }

    @Override
    public CompletionStage<String> retrieveOriginalData(String documentId, String onBehalfOfUser) {

        return CompletableFuture.supplyAsync(() -> "{ \"templateName\": \"fooBar\", \"values\": { \"pageNumber\": \"1\", \"name\": \"" + onBehalfOfUser + "\", \"address\": \"" + documentId + "\", \"pnc\": \"Retrieved From Store\" } }");
    }

    @Override
    public CompletionStage<Integer> lockDocument(String onBehalfOfUser, String documentId) {

        return CompletableFuture.supplyAsync(() -> 200);
    }

    private boolean pdfUpdated;

    @Override
    public CompletionStage<Map<String, String>> updateExistingPdf(Byte[] document, String filename, String onBehalfOfUser, String updatedData, String documentId) {

        pdfUpdated = true;

        return CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "456"));
    }

    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder().
                overrides(
                        bind(PdfGenerator.class).toInstance(this),  // Mock out PdfGenerator to this Test Class
                        bind(DocumentStore.class).toInstance(this)  // Mock out DocumentStore to this Test Class
                ).
                build();
    }
}
