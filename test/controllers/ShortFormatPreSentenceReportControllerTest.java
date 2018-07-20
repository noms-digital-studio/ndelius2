package controllers;

import com.google.common.collect.ImmutableMap;
import helpers.Encryption;
import helpers.JwtHelperTest;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.OffenderApi;
import interfaces.PdfGenerator;
import lombok.val;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static play.api.test.CSRFTokenHelper.addCSRFToken;
import static play.inject.Bindings.bind;
import static play.mvc.Http.RequestBuilder;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
import static utils.OffenderHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ShortFormatPreSentenceReportControllerTest extends WithApplication {

    private DocumentStore documentStore;
    private OffenderApi offenderApi;
    private Function<String, String> encryptor = text -> Encryption.encrypt(text, "ThisIsASecretKey").orElseThrow(() -> new RuntimeException("Encrypt failed"));

    @Test
    public void createNewReport() {

        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any()))
            .willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123")));

        val result = route(app, new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D&crn=v5LH8B7tJKI7fEc9uM76SQ%3D%3D&foo=bar"));

        assertEquals(OK, result.status());
        val content = Helpers.contentAsString(result);
        assertTrue(content.contains(encryptor.apply("Jimmy Jammy Fizz")));
        assertFalse(content.contains("bar"));
    }

    @Test
    public void createNewReport_offenderHasEmptyContactDetails() throws UnsupportedEncodingException {

        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123")));
        given(offenderApi.getOffenderByCrn(any(), eq("X12345")))
            .willReturn(CompletableFuture.completedFuture(anOffenderWithEmptyContactDetails()));

        val crn = URLEncoder.encode(encryptor.apply("X12345"), "UTF-8");
        val result = route(app, new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D&crn="+ crn));

        assertEquals(OK, result.status());
        val content = Helpers.contentAsString(result);
        assertTrue(content.contains(encryptor.apply("Jimmy Jammy Fizz")));
    }

    @Test
    public void createNewReport_offenderHasEmptyAddressList() throws UnsupportedEncodingException {

        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123")));
        given(offenderApi.getOffenderByCrn(any(), eq("X12345")))
            .willReturn(CompletableFuture.completedFuture(anOffenderWithEmptyAddressList()));

        val crn = URLEncoder.encode(encryptor.apply("X12345"), "UTF-8");
        val result = route(app, new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D&crn="+ crn));

        assertEquals(OK, result.status());
        val content = Helpers.contentAsString(result);
        assertTrue(content.contains(encryptor.apply("Jimmy Jammy Fizz")));
    }

    @Test
    public void createNewReport_offenderHasNoMainAddress() throws UnsupportedEncodingException {

        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123")));
        given(offenderApi.getOffenderByCrn(any(), eq("X12345")))
            .willReturn(CompletableFuture.completedFuture(anOffenderWithNoMainAddress()));

        val crn = URLEncoder.encode(encryptor.apply("X12345"), "UTF-8");
        val result = route(app, new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D&crn="+ crn));

        assertEquals(OK, result.status());
        val content = Helpers.contentAsString(result);
        assertTrue(content.contains(encryptor.apply("Jimmy Jammy Fizz")));
    }

    @Test
    public void createNewReport_mainAddressIsUsed() throws UnsupportedEncodingException {

        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123")));
        given(offenderApi.getOffenderByCrn(any(), eq("X12345")))
            .willReturn(CompletableFuture.completedFuture(anOffenderWithMultipleAddresses()));

        val crn = URLEncoder.encode(encryptor.apply("X12345"), "UTF-8");
        val result = route(app, new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D&crn="+ crn));

        assertEquals(OK, result.status());
        val content = Helpers.contentAsString(result);
        assertTrue(content.contains(encryptor.apply("Jimmy Jammy Fizz")));
        assertTrue(content.contains(encryptor.apply("Main address Building\n7 High Street\nNether Edge\nSheffield\nYorkshire\nS10 1LE")));
    }

    @Test
    public void getSampleReportConsumesDtoQueryStrings() {

        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123")));

        val result = route(app, new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?foobar=xyz987&user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D&crn=v5LH8B7tJKI7fEc9uM76SQ%3D%3D"));

        val content = Helpers.contentAsString(result);
        assertEquals(OK, result.status());
        assertTrue(content.contains("v5LH8B7tJKI7fEc9uM76SQ=="));
        assertFalse(content.contains("xyz987"));
    }

    @Test
    public void getSampleReportWithFailedAlfrescoSave() {

        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("message", "Upload blows up for this user")));

        val result = route(app, new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D&crn=v5LH8B7tJKI7fEc9uM76SQ%3D%3D"));

        val content = Helpers.contentAsString(result);
        assertEquals(BAD_REQUEST, result.status());
        assertTrue(content.contains("Upload blows up for this user"));
    }

    @Test
    public void updateReportRetrievesDocumentFromStoreAndUpdatesReportWithOffenderDetailsFromAPI() {

        given(documentStore.retrieveOriginalData(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new DocumentStore.OriginalData("{ \"templateName\": \"fooBar\", \"values\": { \"pageNumber\": \"1\", \"name\": \"Smith, John\", \"address\": \"SHOULD NOT BE IN REPORT\", \"pnc\": \"2018/123456M\", \"startDate\": \"12/12/2017\", \"crn\": \"B56789\", \"entityId\": \"456\", \"dateOfBirth\": \"15/10/1968\", \"age\": \"49\", \"court\": \"Retrieved From Store\" } }", OffsetDateTime.now())));

        try {
            val clearDocumentId = "12345";
            val clearUserName = "John Smith";

            val documentId = URLEncoder.encode(encryptor.apply(clearDocumentId), "UTF-8");
            val onBehalfOfUser = URLEncoder.encode(encryptor.apply(clearUserName), "UTF-8");

            val request = new RequestBuilder().method(GET).
                    uri("/report/shortFormatPreSentenceReport?documentId=" + documentId +
                        "&onBehalfOfUser=" + onBehalfOfUser +
                        "&user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D" +
                        "&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D");

            val content = Helpers.contentAsString(route(app, request));
            assertTrue(content.contains(encryptor.apply("Retrieved From Store")));
            assertTrue(content.contains(encryptor.apply("Jimmy Jammy Fizz")));
            assertTrue(content.contains(encryptor.apply("15/10/1968")));
            assertTrue(content.contains(encryptor.apply("2018/123456N")));
            assertTrue(content.contains("name=\"age\" value=\""+encryptor.apply("49")));
            assertTrue(content.contains(encryptor.apply("Main address Building\n7 High Street\nNether Edge\nSheffield\nYorkshire\nS10 1LE")));
            assertFalse(content.contains(encryptor.apply("SHOULD NOT BE IN REPORT")));

        } catch (Exception ex) {

            fail(ex.getMessage());
        }
    }

    @Test
    public void updateReportRetrievesDocumentFromStoreAndUpdatesReportWithOffenderDetailsFromAPI_addressAndPNCBlank() {

        given(documentStore.retrieveOriginalData(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new DocumentStore.OriginalData("{ \"templateName\": \"fooBar\", \"values\": { \"pageNumber\": \"1\", \"name\": \"Smith, John\", \"address\": \"ADDRESS FROM DOC STORE\", \"pnc\": \"PNC FROM DOC STORE\", \"startDate\": \"12/12/2017\", \"crn\": \"B56789\", \"entityId\": \"456\", \"dateOfBirth\": \"15/10/1968\", \"age\": \"49\", \"court\": \"Retrieved From Store\" } }", OffsetDateTime.now())));
        given(offenderApi.getOffenderByCrn(any(), eq("B56789"))).willReturn(CompletableFuture.completedFuture(anOffenderWithNoContactDetailsAndNoPnc()));

        try {
            val clearDocumentId = "12345";
            val clearUserName = "John Smith";

            val documentId = URLEncoder.encode(encryptor.apply(clearDocumentId), "UTF-8");
            val onBehalfOfUser = URLEncoder.encode(encryptor.apply(clearUserName), "UTF-8");

            val request = new RequestBuilder().method(GET).
                    uri("/report/shortFormatPreSentenceReport?documentId=" + documentId +
                        "&onBehalfOfUser=" + onBehalfOfUser +
                        "&user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D" +
                        "&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D");

            val content = Helpers.contentAsString(route(app, request));
            assertFalse(content.contains(encryptor.apply("PNC FROM DOC STORE")));
            assertFalse(content.contains(encryptor.apply("ADDRESS FROM DOC STORE")));

        } catch (Exception ex) {

            fail(ex.getMessage());
        }
    }

    @Test
    public void updateReportRetrievesDocumentFromStore_crnNotFoundInAPICauses500Error() {

        given(documentStore.retrieveOriginalData(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new DocumentStore.OriginalData("{ \"templateName\": \"fooBar\", \"values\": { \"pageNumber\": \"1\", \"name\": \"Smith, John\", \"address\": \"SHOULD NOT BE IN REPORT\", \"pnc\": \"2018/123456M\", \"startDate\": \"12/12/2017\", \"crn\": \"B56789\", \"entityId\": \"456\", \"dateOfBirth\": \"15/10/1968\", \"age\": \"49\", \"court\": \"Retrieved From Store\" } }", OffsetDateTime.now())));
        given(offenderApi.getOffenderByCrn(any(), eq("B56789"))).willThrow(RuntimeException.class);

        try {
            val clearDocumentId = "12345";
            val clearUserName = "John Smith";

            val documentId = URLEncoder.encode(encryptor.apply(clearDocumentId), "UTF-8");
            val onBehalfOfUser = URLEncoder.encode(encryptor.apply(clearUserName), "UTF-8");

            val request = new RequestBuilder().method(GET).
                    uri("/report/shortFormatPreSentenceReport?documentId=" + documentId +
                        "&onBehalfOfUser=" + onBehalfOfUser +
                        "&user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D" +
                        "&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D");

            Result result = route(app, request);
            assertThat(result.status()).isEqualTo(500);

        } catch (Exception ex) {

            fail(ex.getMessage());
        }
    }

    @Test
    public void postSampleReportPage2TitleOnlyReturnsBadRequest() {

        val formData = ImmutableMap.of(
            "name", "",
            "pageNumber", "2"
        );
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage2AllFieldsReturnsOK() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));

                put("pageNumber", "2");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage3SomeFieldsMissingReturnsBadRequest() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));

                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));

                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage3AllRequiredFieldsReturnsOK() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));

                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));

                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage4SomeFieldsMissingReturnsBadRequest() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));

                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));
                put("otherInformationSource", "true");

                put("pageNumber", "4");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage4AllRequiredFieldsReturnsOK() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));
                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));

                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");

                put("pageNumber", "4");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage5SomeFieldsMissingReturnsBadRequest() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));
                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));

                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");

                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");

                put("pageNumber", "5");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage5AllRequiredFieldsReturnsOK() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));
                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));

                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");

                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");

                put("pageNumber", "5");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage6SomeFieldsMissingReturnsBadRequest() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));
                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));

                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");

                put("patternOfOffending", "Some pattern of offending");

                put("pageNumber", "6");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage6AllFieldsReturnsOK() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));
                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));

                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");

                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");

                put("pageNumber", "6");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage7SomeFieldsMissingReturnsBadRequest() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));
                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));

                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");

                put("issueAccommodation", "false");
                put("issueEmployment", "false");
                put("issueFinance", "false");
                put("issueRelationships", "false");
                put("issueSubstanceAbuse", "false");
                put("issueHealth", "false");
                put("issueBehaviour", "false");
                put("issueOther", "false");

                put("pageNumber", "7");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage7AllRequiredFieldsReturnsOK() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));
                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));

                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");

                put("issueAccommodation", "true");
                put("issueEmployment", "false");
                put("issueFinance", "false");
                put("issueRelationships", "false");
                put("issueSubstanceMisuse", "false");
                put("issueHealth", "false");
                put("issueBehaviour", "false");
                put("issueOther", "false");
                put("experienceTrauma", "no");
                put("experienceTraumaDetails", "");
                put("caringResponsibilities", "no");
                put("caringResponsibilitiesDetails", "");

                put("pageNumber", "7");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage8SomeFieldsMissingReturnsBadRequest() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));
                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));

                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");
                put("issueAccommodation", "true");
                put("issueEmployment", "true");
                put("issueFinance", "true");
                put("issueRelationships", "true");
                put("issueSubstanceMisuse", "true");
                put("issueHealth", "true");
                put("issueBehaviour", "true");
                put("issueOther", "true");


                put("riskOfSeriousHarm", "Some risk of serious harm");
                put("previousSupervisionResponse", "Good");
                put("additionalPreviousSupervision", "Some previous supervision response");

                put("pageNumber", "8");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage8AllRequiredFieldsReturnsOK() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));
                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));

                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");
                put("issueAccommodation", "true");
                put("issueEmployment", "true");
                put("issueFinance", "true");
                put("issueRelationships", "true");
                put("issueSubstanceMisuse", "true");
                put("issueHealth", "true");
                put("issueBehaviour", "true");
                put("issueOther", "true");


                put("likelihoodOfReOffending", "Some likelihood of reoffending");
                put("riskOfSeriousHarm", "Some risk of serious harm");
                put("previousSupervisionResponse", "Good");
                put("additionalPreviousSupervision", "Some previous supervision response");

                put("pageNumber", "8");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage9SomeFieldsMissingReturnsBadRequest() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));
                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));

                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");
                put("issueAccommodation", "true");
                put("issueEmployment", "true");
                put("issueFinance", "true");
                put("issueRelationships", "true");
                put("issueSubstanceMisuse", "true");
                put("issueHealth", "true");
                put("issueBehaviour", "true");
                put("issueOther", "true");

                put("likelihoodOfReOffending", "Some likelihood of reoffending");
                put("riskOfSeriousHarm", "Some risk of serious harm");
                put("previousSupervisionResponse", "Good");
                put("additionalPreviousSupervision", "Some previous supervision response");

                put("pageNumber", "9");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage9AllRequiredFieldsReturnsOK() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));
                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));

                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");
                put("issueAccommodation", "true");
                put("issueEmployment", "true");
                put("issueFinance", "true");
                put("issueRelationships", "true");
                put("issueSubstanceMisuse", "true");
                put("issueHealth", "true");
                put("issueBehaviour", "true");
                put("issueOther", "true");

                put("likelihoodOfReOffending", "Some likelihood of reoffending");
                put("riskOfSeriousHarm", "Some risk of serious harm");
                put("previousSupervisionResponse", "Good");
                put("additionalPreviousSupervision", "Some previous supervision response");

                put("proposal", "Some proposal");
                put("consideredQualityDiversity", "no");

                put("pageNumber", "9");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }


    // PAGE 10
    @Test
    public void postSampleReportPage11SomeFieldsMissingReturnsBadRequest() {

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));
                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));

                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");
                put("issueAccommodation", "true");
                put("issueEmployment", "true");
                put("issueFinance", "true");
                put("issueRelationships", "true");
                put("issueSubstanceMisuse", "true");
                put("issueHealth", "true");
                put("issueBehaviour", "true");
                put("issueOther", "true");

                put("likelihoodOfReOffending", "Some likelihood of reoffending");
                put("riskOfSeriousHarm", "Some risk of serious harm");
                put("previousSupervisionResponse", "Good");
                put("additionalPreviousSupervision", "Some previous supervision response");
                put("proposal", "Some proposal");

                put("office", "Sheffield probation office");
                put("officePhone", "0114 114 114");
                put("counterSignature", "Some other person");

                put("pageNumber", "11");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage11AllRequiredFieldsReturnsOK() {

        given(documentStore.updateExistingPdf(any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "456")));

        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("age", encryptor.apply("41"));
                put("address", encryptor.apply("10 High Street"));
                put("crn", encryptor.apply("B56789"));
                put("pnc", encryptor.apply("98793030"));
                put("court", encryptor.apply("Manchester and Salford Magistrates Court"));
                put("dateOfHearing", encryptor.apply("01/02/2017"));
                put("localJusticeArea", encryptor.apply("Greater Manchester"));

                put("interviewInformationSource", "true");
                put("serviceRecordsInformationSource", "true");
                put("cpsSummaryInformationSource", "true");
                put("oasysAssessmentsInformationSource", "true");
                put("previousConvictionsInformationSource", "true");
                put("victimStatementInformationSource", "true");
                put("childrenServicesInformationSource", "true");
                put("policeInformationSource", "true");
                put("sentencingGuidelinesSource", "true");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "These notes are spelled correctly");
                put("mainOffence", "Some offence");
                put("otherOffences", "Some other offences");
                put("offenceSummary", "Some offence summary");
                put("offenceAnalysis", "Some offence analysis");
                put("patternOfOffending", "Some pattern of offending");
                put("issueAccommodation", "true");
                put("issueEmployment", "true");
                put("issueFinance", "true");
                put("issueRelationships", "true");
                put("issueSubstanceMisuse", "true");
                put("issueHealth", "true");
                put("issueBehaviour", "true");
                put("issueOther", "true");
                put("experienceTrauma", "no");
                put("experienceTraumaDetails", "");
                put("caringResponsibilities", "no");
                put("caringResponsibilitiesDetails", "");

                put("likelihoodOfReOffending", "Some likelihood of reoffending");
                put("riskOfSeriousHarm", "Some risk of serious harm");
                put("previousSupervisionResponse", "Good");
                put("additionalPreviousSupervision", "Some previous supervision response");
                put("proposal", "Some proposal");
                put("consideredQualityDiversity", "yes");

                put("reportAuthor", "Arthur Author");
                put("office", "Sheffield probation office");
                put("officePhone", "0114 114 114");
                put("counterSignature", "Some other person");
                put("reportDate", "21/06/2017");

                put("pageNumber", "11");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
    }


    @Override
    protected Application provideApplication() {
        PdfGenerator pdfGenerator = mock(PdfGenerator.class);
        given(pdfGenerator.generate(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));

        documentStore = mock(DocumentStore.class);
        offenderApi = mock(OffenderApi.class);
        given(offenderApi.logon(any())).willReturn(CompletableFuture.completedFuture(JwtHelperTest.generateToken()));
        given(offenderApi.getOffenderByCrn(any(), eq("B56789"))).willReturn(CompletableFuture.completedFuture(anOffenderWithMultipleAddresses()));

        return new GuiceApplicationBuilder().
                overrides(
                        bind(PdfGenerator.class).toInstance(pdfGenerator),
                        bind(DocumentStore.class).toInstance(documentStore),
                        bind(AnalyticsStore.class).toInstance(mock(AnalyticsStore.class)),
                        bind(OffenderApi.class).toInstance(offenderApi))
                .configure("params.user.token.valid.duration", "100000d")
                .build();
    }

}
