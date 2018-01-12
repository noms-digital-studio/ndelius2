package views;

import com.google.common.collect.ImmutableList;
import helpers.Encryption;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.WithApplication;
import utils.SimpleAnalyticsStoreMock;
import utils.SimpleDocumentStoreMock;
import utils.SimplePdfGeneratorMock;

import java.util.HashMap;
import java.util.function.Function;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.api.test.CSRFTokenHelper.addCSRFToken;
import static play.inject.Bindings.bind;
import static play.test.Helpers.*;

@RunWith(MockitoJUnitRunner.class)
public class SentencingCourtDetailsTest extends WithApplication {

    private static final ImmutableList<String> LONDON_LOCALES =
        ImmutableList.<String>builder()
            .add("West London")
            .add("North East London")
            .add("North West London")
            .add("South West London")
            .add("South London")
            .add("South East London")
            .add("Central London")
            .add("North London")
            .add("East London")
            .build();

    private Function<String, String> encryptor = plainText -> Encryption.encrypt(plainText, "ThisIsASecretKey");

    @Test
    public void localJusticeAreaDropDownContainsLondonLocales() {
        val request = givenARequestForTheSentencingCourtDetailsPage();

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
        LONDON_LOCALES.forEach((locale) -> assertTrue(format("should contain [%s]", locale), contentAsString(result).contains(locale)));
    }

    @Test
    public void localJusticeAreaDropDownContainsSouthYorkshire() {
        val request = givenARequestForTheSentencingCourtDetailsPage();

        val result = route(app, addCSRFToken(request));

        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains("South Yorkshire"));
    }

    private Http.RequestBuilder givenARequestForTheSentencingCourtDetailsPage() {
        val formData = new HashMap<String, String>() {
            {
                put("onBehalfOfUser", encryptor.apply("johnsmith"));
                put("entityId", encryptor.apply("12345"));
                put("documentId", encryptor.apply("67890"));
                put("name", encryptor.apply("John Smith"));
                put("dateOfBirth", encryptor.apply("06/02/1976"));
                put("pageNumber", "2");
                put("jumpNumber", "3");
            }
        };
        return new Http.RequestBuilder().method(POST).bodyForm(formData).uri("/report/shortFormatPreSentenceReport");
    }

    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder().
            overrides(
                bind(PdfGenerator.class).toInstance(new SimplePdfGeneratorMock()),
                bind(DocumentStore.class).toInstance(new SimpleDocumentStoreMock()),
                bind(AnalyticsStore.class).toInstance(new SimpleAnalyticsStoreMock())
            )
            .build();
    }
}
