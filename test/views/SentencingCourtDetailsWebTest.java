package views;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import views.pages.SentencingCourtDetailsPage;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static play.inject.Bindings.bind;

public class SentencingCourtDetailsWebTest extends WithIE8Browser {
    private SentencingCourtDetailsPage sentencingCourtDetailsPage;

    private static final List<String> SORTED_LONDON_LOCALES =
            ImmutableList.<String>builder()
                    .add("Central London (Hammersmith and Fulham and Kensington and Chelsea, City of London, City of Westminster)")
                    .add("East London (Hackney and Tower Hamlets, Newham, Waltham Forest)")
                    .add("North East London (Barking, Havering, Redbridge)")
                    .add("North London (Camden and Islington, Enfield, Haringey)")
                    .add("North West London (Barnet, Brent, Harrow Gore)")
                    .add("South East London (Greenwich and Lewisham, Bexley, Bromley)")
                    .add("South London (Croydon, Lambeth and Southwark, Sutton)")
                    .add("South West London (Kingston, Merton, Richmon-upon-Thames, Wandsworth)")
                    .add("West London (Ealing, Hounslow, Hillingdon)")
                    .build()
                    .stream()
                    .sorted()
                    .collect(Collectors.toList());

    @Before
    public void before() {
        sentencingCourtDetailsPage = new SentencingCourtDetailsPage(browser);
    }

    @Test
    public void localJusticeAreaDropDownContainsLondonRegionsInOrder() {
        val options = sentencingCourtDetailsPage.
                navigateHere().
                localJusticeAreas();

        assertThat(options).containsSequence(SORTED_LONDON_LOCALES);
    }

    @Override
    protected Application provideApplication() {
        PdfGenerator pdfGenerator = mock(PdfGenerator.class);
        given(pdfGenerator.generate(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new Byte[0]));

        DocumentStore documentStore = mock(DocumentStore.class);
        given(documentStore.updateExistingPdf(any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "456")));
        given(documentStore.uploadNewPdf(any(), any(), any(), any(), any(), any())).willReturn(CompletableFuture.supplyAsync(() -> ImmutableMap.of("ID", "123")));
        given(documentStore.retrieveOriginalData(any(), any())).willReturn(CompletableFuture.supplyAsync(() -> new DocumentStore.OriginalData("{ \"templateName\": \"fooBar\", \"values\": { \"pageNumber\": \"1\", \"name\": \"Smith, John\", \"address\": \"456\", \"pnc\": \"Retrieved From Store\", \"startDate\": \"12/12/2017\", \"crn\": \"1234\", \"entityId\": \"456\", \"dateOfBirth\": \"15/10/1968\", \"age\": \"49\" } }", OffsetDateTime.now())));

        return new GuiceApplicationBuilder().
            overrides(
                bind(PdfGenerator.class).toInstance(pdfGenerator),
                bind(DocumentStore.class).toInstance(documentStore),
                bind(AnalyticsStore.class).toInstance(mock(AnalyticsStore.class))
            ).build();
    }
}
