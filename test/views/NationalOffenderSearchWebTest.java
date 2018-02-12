package views;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithBrowser;
import services.DeliusOffenderApi;
import views.pages.NationalSearchPage;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;

@RunWith(MockitoJUnitRunner.class)
public class NationalOffenderSearchWebTest extends WithBrowser {
    private NationalSearchPage nationalSearchPage;
    @Mock
    private RestHighLevelClient restHighLevelClient;
    @Mock
    private DeliusOffenderApi deliusOffenderApi;

    private String BEARER = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJjbj1mYWtlLnVzZXIsY249VXNlcnMsZGM9bW9qLGRjPWNvbSIsInVpZCI6ImZha2UudXNlciIsImV4cCI6MTUxNzYzMTkzOX0=.FsI0VbLbqLRUGo7GXDEr0hHLvDRJjMQWcuEJCCaevXY1KAyJ_05I8V6wE6UqH7gB1Nq2Y4tY7-GgZN824dEOqQ";
    @Before
    public void before() {
        when(deliusOffenderApi.logon(anyString())).thenReturn(CompletableFuture.completedFuture(BEARER));
        nationalSearchPage = new NationalSearchPage(browser);
        nationalSearchPage.navigateHere();
    }

    @Test
    public void searchBoxRendered() {
       assertThat(nationalSearchPage.hasSearchBox()).isTrue();
    }


    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().
            overrides(
                bind(RestHighLevelClient.class).toInstance(restHighLevelClient),
                bind(DeliusOffenderApi.class).toInstance(deliusOffenderApi)
            ).configure("params.user.token.valid.duration", "100000d")
            .build();
    }

}
