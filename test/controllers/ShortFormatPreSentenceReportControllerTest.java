package controllers;

import com.google.common.collect.ImmutableMap;
import interfaces.PdfGenerator;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import lombok.val;
import org.junit.Test;
import play.Application;
import play.filters.csrf.*;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import static org.junit.Assert.*;
import static play.inject.Bindings.bind;
import static play.mvc.Http.RequestBuilder;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class ShortFormatPreSentenceReportControllerTest extends WithApplication implements PdfGenerator {

    @Test
    public void getSampleReportOK() {

        val request = new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void getSampleReportConsumesDtoQueryStrings() {

        val request = new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?name=Alan%20Smith&foobar=xyz987");

        val content = Helpers.contentAsString(route(app, request));

        assertTrue(content.contains("Alan Smith"));
        assertFalse(content.contains("xyz987"));
    }

    @Test
    public void postSampleReportPage1TitleOnlyReturnsBadRequest() {

        val formData = ImmutableMap.of(
                "name", "",
                "pageNumber", "1"
        );
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage1AllFieldsReturnsOK() {

        val formData = new HashMap<String, String>() {
            {
                put("name", "John Smith");
                put("dateOfBirth", "06/02/1976");
                put("age", "41");
                put("address", "10 High Street");
                put("crn", "B56789");
                put("pcn", "98793030");
                put("pageNumber", "1");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage2SomeFieldsMissingReturnsBadRequest() {

        val formData = new HashMap<String, String>() {
            {
                put("name", "John Smith");
                put("dateOfBirth", "06/02/1976");
                put("age", "41");
                put("address", "10 High Street");
                put("crn", "B56789");
                put("pcn", "98793030");
                put("court", "Manchester and Salford Magistrates Court");
                put("pageNumber", "2");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage2AllRequiredFieldsReturnsOK() {

        val formData = new HashMap<String, String>() {
            {
                put("name", "John Smith");
                put("dateOfBirth", "06/02/1976");
                put("age", "41");
                put("address", "10 High Street");
                put("crn", "B56789");
                put("pcn", "98793030");
                put("court", "Manchester and Salford Magistrates Court");
                put("dateOfHearing", "01/02/2017");
                put("localJusticeArea", "Greater Manchester");
                put("pageNumber", "2");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage3SomeFieldsMissingReturnsBadRequest() {

        val formData = new HashMap<String, String>() {
            {
                put("name", "John Smith");
                put("dateOfBirth", "06/02/1976");
                put("age", "41");
                put("address", "10 High Street");
                put("crn", "B56789");
                put("pcn", "98793030");
                put("court", "Manchester and Salford Magistrates Court");
                put("dateOfHearing", "01/02/2017");
                put("localJusticeArea", "Greater Manchester");
                put("otherInformationSource", "true");
                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage3WithSpellingMistakeReturnsBadRequest() {

        val formData = new HashMap<String, String>() {
            {
                put("name", "John Smith");
                put("dateOfBirth", "06/02/1976");
                put("age", "41");
                put("address", "10 High Street");
                put("crn", "B56789");
                put("pcn", "98793030");
                put("court", "Manchester and Salford Magistrates Court");
                put("dateOfHearing", "01/02/2017");
                put("localJusticeArea", "Greater Manchester");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "This texxt has speeling mistakes");
                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage3WithSpellingMistakeAndOverrideReturnsOK() {

        val formData = new HashMap<String, String>() {
            {
                put("name", "John Smith");
                put("dateOfBirth", "06/02/1976");
                put("age", "41");
                put("address", "10 High Street");
                put("crn", "B56789");
                put("pcn", "98793030");
                put("court", "Manchester and Salford Magistrates Court");
                put("dateOfHearing", "01/02/2017");
                put("localJusticeArea", "Greater Manchester");
                put("otherInformationSource", "true");
                put("otherInformationDetails", "This texxt has speeling mistakes");
                put("ignoreOtherInformationDetailsSpelling", "true");
                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage3AllRequiredFieldsReturnsOK() {

        val formData = new HashMap<String, String>() {
            {
                put("name", "John Smith");
                put("dateOfBirth", "06/02/1976");
                put("age", "41");
                put("address", "10 High Street");
                put("crn", "B56789");
                put("pcn", "98793030");
                put("court", "Manchester and Salford Magistrates Court");
                put("dateOfHearing", "01/02/2017");
                put("localJusticeArea", "Greater Manchester");
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
                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage4AllRequiredFieldsReturnsOKAndPdfGenerated() {

        val formData = new HashMap<String, String>() {
            {
                put("name", "John Smith");
                put("dateOfBirth", "06/02/1976");
                put("age", "41");
                put("address", "10 High Street");
                put("crn", "B56789");
                put("pcn", "98793030");
                put("court", "Manchester and Salford Magistrates Court");
                put("dateOfHearing", "01/02/2017");
                put("localJusticeArea", "Greater Manchester");
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
                put("pageNumber", "4");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");
        pdfGenerated = false;

        val result = route(app, addCsrfToken(request));

        assertEquals(OK, result.status());
        assertEquals("application/pdf", result.contentType().orElse(""));
        assertTrue(pdfGenerated);
    }

    private boolean pdfGenerated;

    @Override
    public <T> CompletionStage<Byte[]> generate(String templateName, T values) {

        pdfGenerated = true;

        return CompletableFuture.supplyAsync(() -> new Byte[0]);    // Mocked PdfGenerator returns empty Byte array
    }

    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder().
                overrides(bind(PdfGenerator.class).toInstance(this)). // Mock out PdfGenerator to this Test Class
                build();
    }

    private RequestBuilder addCsrfToken(RequestBuilder requestBuilder) {
        final CSRFFilter csrfFilter = app.injector().instanceOf(CSRFFilter.class);
        final CSRFConfig csrfConfig = app.injector().instanceOf(CSRFConfigProvider.class).get();
        final String token = csrfFilter.tokenProvider().generateToken();

        requestBuilder.tag(CSRF.Token$.MODULE$.NameRequestTag(), csrfConfig.tokenName());
        requestBuilder.tag(CSRF.Token$.MODULE$.RequestTag(), token);
        requestBuilder.header(csrfConfig.headerName(), token);

        return requestBuilder;
    }
}

