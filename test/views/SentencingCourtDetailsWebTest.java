package views;

import com.google.common.collect.ImmutableList;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithBrowser;
import utils.SimpleAnalyticsStoreMock;
import utils.SimpleDocumentStoreMock;
import utils.SimplePdfGeneratorMock;
import views.pages.SentencingCourtDetailsPage;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static play.inject.Bindings.bind;

@RunWith(MockitoJUnitRunner.class)
public class SentencingCourtDetailsWebTest extends WithBrowser {
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
        return new GuiceApplicationBuilder().
            overrides(
                bind(PdfGenerator.class).toInstance(new SimplePdfGeneratorMock()),
                bind(DocumentStore.class).toInstance(new SimpleDocumentStoreMock()),
                bind(AnalyticsStore.class).toInstance(new SimpleAnalyticsStoreMock())
            )
            .build();
    }
}
