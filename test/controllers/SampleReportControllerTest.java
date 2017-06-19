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

public class SampleReportControllerTest extends WithApplication implements PdfGenerator {

    @Test
    public void getSampleReportOK() {

        val request = new RequestBuilder().method(GET).uri("/sampleReport");

        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void getSampleReportConsumesDtoQueryStrings() {

        val request = new RequestBuilder().method(GET).uri("/sampleReport?identifier=abc123&foobar=xyz987");

        val content = Helpers.contentAsString(route(app, request));

        assertTrue(content.contains("abc123"));
        assertFalse(content.contains("xyz987"));
    }

    @Test
    public void postSampleReportPage1TitleOnlyReturnsBadRequest() {

        val formData = ImmutableMap.of(
                "salutation", "Mr",
                "pageNumber", "1"
        );
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/sampleReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage1AllFieldsReturnsOK() {

        val formData = ImmutableMap.of(
                "salutation", "Mr",
                "forename", "John",
                "surname", "Smith",
                "pageNumber", "1"
        );
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/sampleReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage2SomeFieldsMissingReturnsBadRequest() {

        val formData = ImmutableMap.of(
                "salutation", "Mr",
                "forename", "John",
                "surname", "Smith",
                "address1", "10 High Street",
                "pageNumber", "2"
        );
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/sampleReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage2AllRequiredFieldsReturnsOK() {

        val formData = new HashMap<String, String>() {
            {
                put("salutation", "Mr");
                put("forename", "John");
                put("surname", "Smith");
                put("address1", "10 High Street");
                put("address2", "Some Town");
                put("pageNumber", "2");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/sampleReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage3SomeFieldsMissingReturnsBadRequest() {

        val formData = new HashMap<String, String>() {
            {
                put("salutation", "Mr");
                put("forename", "John");
                put("surname", "Smith");
                put("address1", "10 High Street");
                put("address2", "Some Town");
                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/sampleReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage3WithSpellingMistakeReturnsBadRequest() {

        val formData = new HashMap<String, String>() {
            {
                put("salutation", "Mr");
                put("forename", "John");
                put("surname", "Smith");
                put("address1", "10 High Street");
                put("address2", "Some Town");
                put("caseNumber", "12345");
                put("letterNotes", "This texxt has speeling mistakes");
                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/sampleReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postSampleReportPage3WithSpellingMistakeAndOverrideReturnsOK() {

        val formData = new HashMap<String, String>() {
            {
                put("salutation", "Mr");
                put("forename", "John");
                put("surname", "Smith");
                put("address1", "10 High Street");
                put("address2", "Some Town");
                put("caseNumber", "12345");
                put("letterNotes", "This texxt has speeling mistakes");
                put("ignoreNotesErrors", "true");
                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/sampleReport");

        val result = route(app, addCsrfToken(request));

        assertEquals(OK, result.status());
    }

    @Test
    public void postSampleReportPage3AllRequiredFieldsReturnsOKAndPdfGenerated() {

        val formData = new HashMap<String, String>() {
            {
                put("salutation", "Mr");
                put("forename", "John");
                put("surname", "Smith");
                put("address1", "10 High Street");
                put("address2", "Some Town");
                put("caseNumber", "12345");
                put("letterNotes", "These notes are spelled correctly");
                put("pageNumber", "3");
            }
        };
        val request = new RequestBuilder().method(POST).bodyForm(formData).uri("/sampleReport");
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

