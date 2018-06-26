package views;

import com.google.common.collect.ImmutableMap;
import helpers.Encryption;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import lombok.val;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static play.api.test.CSRFTokenHelper.addCSRFToken;
import static play.inject.Bindings.bind;
import static play.test.Helpers.*;

public class SessionMismatchTest extends WithApplication {

    private Function<String, String> encryptor = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey").orElseThrow(() -> new RuntimeException("Encrypt failed"));
    private DocumentStore documentStore;

    @Test
    public void postRequestWithMatchingRequestAndSessionTokenIsOK() {
        val result = route(app, addCSRFToken(postRequestWith("match", "match")));

        assertEquals(OK, result.status());
        verify(documentStore).updateExistingPdf(any(), any(), any(), any(), any());
    }

    @Test
    public void postRequestWithMismatchingRequestAndSessionTokenIsBadRequest() {
        val result = route(app, addCSRFToken(postRequestWith("match", "mismatch")));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void postRequestWithMismatchingRequestAndSessionTokenRetunsErrorMessage() {
        val result = route(app, addCSRFToken(postRequestWith("match", "mismatch")));

        assertThat(contentAsString(result)).contains("This report has been amended in another window");
    }

    @Test
    public void postRequestWithMismatchingRequestAndSessionTokenDoesNotUpdateDocument() {
        val result = route(app, addCSRFToken(postRequestWith("match", "mismatch")));

        verify(documentStore, never()).updateExistingPdf(any(), any(), any(), any(), any());
    }

    @Test
    public void postRequestWithMissingSessionTokenIsOK() {
        val result = route(app, addCSRFToken(postRequestWith("", "match")));

        assertEquals(OK, result.status());
        verify(documentStore).updateExistingPdf(any(), any(), any(), any(), any());
    }

    @Test
    public void postRequestWithMissingRequestTokenIsOK() {
        val result = route(app, addCSRFToken(postRequestWith("match", "")));

        assertEquals(OK, result.status());
        verify(documentStore).updateExistingPdf(any(), any(), any(), any(), any());
    }


    @Test
    public void saveRequestWithMatchingRequestAndSessionTokenIsOK() {
        val result = route(app, addCSRFToken(saveRequestWith("match", "match")));

        assertEquals(OK, result.status());
        verify(documentStore).updateExistingPdf(any(), any(), any(), any(), any());
    }

    @Test
    public void saveRequestWithMismatchingRequestAndSessionTokenIsBadRequest() {
        val result = route(app, addCSRFToken(saveRequestWith("match", "mismatch")));

        assertEquals(BAD_REQUEST, result.status());
        verify(documentStore, never()).updateExistingPdf(any(), any(), any(), any(), any());
    }

    @Test
    public void saveRequestWithMissingSessionTokenIsOK() {
        val result = route(app, addCSRFToken(saveRequestWith("", "match")));

        assertEquals(OK, result.status());
        verify(documentStore).updateExistingPdf(any(), any(), any(), any(), any());
    }

    @Test
    public void saveRequestWithMissingRequestTokenIsOK() {
        val result = route(app, addCSRFToken(saveRequestWith("match", "")));

        assertEquals(OK, result.status());
        verify(documentStore).updateExistingPdf(any(), any(), any(), any(), any());
    }


    private Http.RequestBuilder postRequestWith(String sessionToken, String inSessionToken) {
        return new Http.RequestBuilder().
                method(POST).
                bodyForm(formData(sessionToken)).
                uri("/report/shortFormatPreSentenceReport").
                session("sessionToken", inSessionToken);
    }

    private Http.RequestBuilder saveRequestWith(String sessionToken, String inSessionToken) {
        return new Http.RequestBuilder().
                method(POST).
                bodyForm(formData(sessionToken)).
                uri("/report/shortFormatPreSentenceReport/save").
                session("sessionToken", inSessionToken);
    }

    private Map<String, String> formData(String sessionToken) {
        return new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("pageNumber", "2");
                put("jumpNumber", "3");
                put("sessionToken", sessionToken);
            }
        };
    }

    @Override
    protected Application provideApplication() {
        PdfGenerator pdfGenerator = mock(PdfGenerator.class);
        given(pdfGenerator.generate(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));

        documentStore = mock(DocumentStore.class);
        given(documentStore.updateExistingPdf(any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "456")));

        return new GuiceApplicationBuilder().
            overrides(
                bind(PdfGenerator.class).toInstance(pdfGenerator),
                bind(DocumentStore.class).toInstance(documentStore),
                bind(AnalyticsStore.class).toInstance(mock(AnalyticsStore.class))
            ).build();
    }
}
