package controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import helpers.Encryption;
import helpers.JwtHelperTest;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.OffenderApi;
import interfaces.OffenderApi.CourtAppearances;
import interfaces.PdfGenerator;
import lombok.val;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.WithApplication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static play.inject.Bindings.bind;
import static play.mvc.Http.RequestBuilder;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.BAD_REQUEST;
import static play.test.Helpers.GET;
import static play.test.Helpers.UNAUTHORIZED;
import static play.test.Helpers.route;
import static utils.OffenderHelper.anOffenderWithNoContactDetails;

public class ShortFormatPreSentenceReportController_userCredsValidation_Test extends WithApplication {

    private DocumentStore documentStore;
    private OffenderApi offenderApi;
    private Function<String, String> encryptor = text -> Encryption.encrypt(text, "ThisIsASecretKey").orElseThrow(() -> new RuntimeException("Encrypt failed"));

    @Test
    public void withoutAUserTokenReturns401() {
        val request = new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D");
        val result = route(app, request);

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void withoutATimeTokenReturns401() {
        val request = new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D");
        val result = route(app, request);

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void badUserTokenReturns400Response() {
        val request = new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?user=foobar&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D");
        val result = route(app, request);

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void badTimeTokenReturns400Response() {
        val request = new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=foobar");
        val result = route(app, request);

        assertEquals(BAD_REQUEST, result.status());
    }


    @Test
    public void timeTokenInFuture30Mins200Response() throws UnsupportedEncodingException {
        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any()))
            .willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123")));

        RequestBuilder request = buildReportRequest(30);

        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void timeTokenInPast30MinsIsOk() throws UnsupportedEncodingException {
        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any()))
            .willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123")));


        Http.RequestBuilder request = buildReportRequest(-30);
        val result = route(app, request);

        assertEquals(OK, result.status());
    }

    @Test
    public void timeTokenInFuture61MinsIsError() throws UnsupportedEncodingException {
        Http.RequestBuilder request = buildReportRequest(61);
        val result = route(app, request);

        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void timeTokenInPast61MinsIsError() throws UnsupportedEncodingException {
        Http.RequestBuilder request = buildReportRequest(-61);
        val result = route(app, request);

        assertEquals(UNAUTHORIZED, result.status());
    }

    @Override
    protected Application provideApplication() {
        PdfGenerator pdfGenerator = mock(PdfGenerator.class);
        given(pdfGenerator.generate(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));

        documentStore = mock(DocumentStore.class);
        offenderApi = mock(OffenderApi.class);
        given(offenderApi.logon(any())).willReturn(CompletableFuture.completedFuture(JwtHelperTest.generateToken()));
        given(offenderApi.getOffenderByCrn(any(), eq("B56789"))).willReturn(CompletableFuture.completedFuture(anOffenderWithNoContactDetails()));
        given(offenderApi.getCourtAppearancesByCrn(any(), eq("B56789")))
            .willReturn(CompletableFuture.completedFuture(CourtAppearances.builder().items(ImmutableList.of()).build()));

        return new GuiceApplicationBuilder().
                overrides(
                        bind(PdfGenerator.class).toInstance(pdfGenerator),
                        bind(DocumentStore.class).toInstance(documentStore),
                        bind(AnalyticsStore.class).toInstance(mock(AnalyticsStore.class)),
                        bind(OffenderApi.class).toInstance(offenderApi))
                .configure("params.user.token.valid.duration", "60m")
                .build();
    }

    private RequestBuilder buildReportRequest(int minutesDrift) throws UnsupportedEncodingException {
        val encryptedTime = URLEncoder.encode(encryptor.apply(String.valueOf(Instant.now().toEpochMilli() + (1000 * 60 * minutesDrift))), "UTF-8");
        return new RequestBuilder().method(GET).uri("/report/shortFormatPreSentenceReport?user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&crn=v5LH8B7tJKI7fEc9uM76SQ%3D%3D&t=" + encryptedTime);
    }
}
