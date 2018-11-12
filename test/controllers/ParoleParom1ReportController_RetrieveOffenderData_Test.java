package controllers;

import bdd.wiremock.AlfrescoDocumentBuilder;
import com.google.common.collect.ImmutableMap;
import com.mongodb.rx.client.MongoClient;
import helpers.Encryption;
import helpers.JwtHelperTest;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.OffenderApi;
import interfaces.PdfGenerator;
import interfaces.PrisonerApi;
import lombok.val;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.Helpers;
import play.test.WithApplication;
import utils.InstitutionalReportHelpers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;
import static utils.InstitutionalReportHelpers.anInstitutionalReport;
import static utils.OffenderHelper.anOffenderWithMultipleAddresses;
import static utils.PrisonerHelper.offenderInPrison;

@RunWith(MockitoJUnitRunner.class)
public class ParoleParom1ReportController_RetrieveOffenderData_Test extends WithApplication {
    @Mock
    private DocumentStore documentStore;
    @Mock
    private OffenderApi offenderApi;
    @Mock
    private PrisonerApi prisonerApi;
    @Mock
    private PdfGenerator pdfGenerator;

    private Function<String, String> encryptor = text -> Encryption.encrypt(text, "ThisIsASecretKey").orElseThrow(() -> new RuntimeException("Encrypt failed"));

    @Before
    public void setUp() {
        given(pdfGenerator.generate(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));
        given(offenderApi.logon(any())).willReturn(CompletableFuture.completedFuture(JwtHelperTest.generateToken()));
        given(offenderApi.getOffenderByCrn(any(), any())).willReturn(CompletableFuture.completedFuture(anOffenderWithMultipleAddresses()));
        given(offenderApi.getInstitutionalReport(any(), any(), any())).willReturn(CompletableFuture.completedFuture(anInstitutionalReport()));
        given(prisonerApi.getOffenderByNomsNumber(any())).willReturn(CompletableFuture.completedFuture(Optional.of(offenderInPrison())));
        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123")));
    }

    @Test
    public void newReportsContainOffenceFromApi() {
        given(offenderApi.getInstitutionalReport(any(), any(), any()))
            .willReturn(CompletableFuture.completedFuture(InstitutionalReportHelpers.anInstitutionalReportWithOffence("desc", "code123", "2018-12-11")));

        val result = route(app, new Http.RequestBuilder().method(GET).uri("/report/paroleParom1Report?user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D&crn=v5LH8B7tJKI7fEc9uM76SQ%3D%3D&entityId=J5ASYr85DPHjd94ZC3ShNw%3D%3D"));

        assertEquals(OK, result.status());
        val content = Helpers.contentAsString(result);
        assertThat(content).contains("name=\"prisonerDetailsOffence\" value=\"desc (code123) - 11/12/2018\"");
    }

    @Test
    public void existingReportsDoNotOverWriteDocumentContentsWithApi() throws UnsupportedEncodingException {
        given(documentStore.retrieveOriginalData(any(), any())).willReturn(CompletableFuture.supplyAsync(
                () -> new DocumentStore.OriginalData(
                        AlfrescoDocumentBuilder.standardDocument().withValuesItem("prisonerDetailsOffence", "An offence from the doc store").userData(), OffsetDateTime.now())));

        val documentId = URLEncoder.encode(encryptor.apply("12345"), "UTF-8");
        val onBehalfOfUser = URLEncoder.encode(encryptor.apply("JohnSmithNPS"), "UTF-8");

        val result = route(app, new Http.RequestBuilder().method(GET).uri(String.format("/report/paroleParom1Report?documentId=%s&onBehalfOfUser=%s&user=lJqZBRO%%2F1B0XeiD2PhQtJg%%3D%%3D&t=T2DufYh%%2B%%2F%%2F64Ub6iNtHDGg%%3D%%3D", documentId, onBehalfOfUser)));

        val content = Helpers.contentAsString(result);
        assertThat(content).contains("name=\"prisonerDetailsOffence\" value=\"An offence from the doc store\"");
    }


    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().
                overrides(
                        bind(PdfGenerator.class).toInstance(pdfGenerator),
                        bind(DocumentStore.class).toInstance(documentStore),
                        bind(AnalyticsStore.class).toInstance(mock(AnalyticsStore.class)),
                        bind(OffenderApi.class).toInstance(offenderApi),
                        bind(PrisonerApi.class).toInstance(prisonerApi),
                        bind(RestHighLevelClient.class).toInstance(mock(RestHighLevelClient.class)),
                        bind(MongoClient.class).toInstance(mock(MongoClient.class))
                )
                .configure("params.user.token.valid.duration", "100000d")
                .configure("prisoner.api.provider", "custody")
                .configure("custody.api.auth.username", "username")
                .configure("custody.api.auth.password", "password")
                .build();
    }


}