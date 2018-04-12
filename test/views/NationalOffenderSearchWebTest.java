package views;

import com.google.common.collect.ImmutableMap;
import helpers.FutureListener;
import helpers.JwtHelperTest;
import lombok.val;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHits;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import services.DeliusOffenderApi;
import services.NomisPrisonerApi;
import views.pages.NationalSearchPage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static helpers.JwtHelperTest.generateToken;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static play.inject.Bindings.bind;
import static utils.data.OffenderESDataFactory.getSearchHitArray;

@RunWith(MockitoJUnitRunner.class)
public class NationalOffenderSearchWebTest extends WithChromeBrowser {
    private NationalSearchPage nationalSearchPage;
    @Mock
    private RestHighLevelClient restHighLevelClient;
    @Mock
    private DeliusOffenderApi deliusOffenderApi;
    @Mock
    private SearchResponse searchResponse;
    @Mock
    private NomisPrisonerApi nomisPrisonerApi;

    @Before
    public void before() {
        when(deliusOffenderApi.logon(anyString())).thenReturn(CompletableFuture.completedFuture(generateToken()));
        doAnswer(invocation -> {
            val listener = (FutureListener)invocation.getArguments()[1];
            listener.onResponse(searchResponse);
            return null;
        }).when(restHighLevelClient).searchAsync(any(), any());

        when(nomisPrisonerApi.getImage(any())).thenReturn(CompletableFuture.completedFuture(new byte[]{}));

        nationalSearchPage = new NationalSearchPage(browser);
        nationalSearchPage.navigateHere();
    }

    @Test
    @Ignore
    public void searchBoxRendered() {
       assertThat(nationalSearchPage.hasSearchBox()).isTrue();
    }

    @Test
    @Ignore
    public void searchResultsAreDisplayedAfterEnteringSearchTerm() {
        // GIVEN
        when(searchResponse.getHits()).thenReturn(new SearchHits(getSearchHitArray(
                ImmutableMap.of(
                        "firstName", "John",
                        "surname", "Smith",
                        "offenderId", 1,
                        "crn", "X0001") ,
                ImmutableMap.of(
                        "firstName", "Johnny",
                        "surname", "Smith",
                        "offenderId", 2,
                        "crn", "X0002")
        ), 2, 42));

        // WHEN
        nationalSearchPage.fillSearchTerm("John Smith");
        browser
                .fluentWait()
                .withTimeout(5, TimeUnit.SECONDS)
                .until(
                        (driver) -> nationalSearchPage.hasOffenderResults());

        // THAN
        assertThat(nationalSearchPage.getSummaryTitle(1)).contains("John");
        assertThat(nationalSearchPage.getSummaryTitle(1)).contains("Smith");
        assertThat(nationalSearchPage.getSummaryTitle(1)).contains("X0001");

        assertThat(nationalSearchPage.getSummaryTitle(2)).contains("Johnny");
        assertThat(nationalSearchPage.getSummaryTitle(2)).contains("Smith");
        assertThat(nationalSearchPage.getSummaryTitle(2)).contains("X0002");
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().
            overrides(
                bind(RestHighLevelClient.class).toInstance(restHighLevelClient),
                bind(DeliusOffenderApi.class).toInstance(deliusOffenderApi),
                bind(NomisPrisonerApi.class).toInstance(nomisPrisonerApi)
            ).configure("params.user.token.valid.duration", "100000d")
            .build();
    }

}
