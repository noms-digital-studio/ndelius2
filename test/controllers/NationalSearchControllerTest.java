package controllers;

import data.offendersearch.OffenderSearchResult;
import interfaces.Search;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

@RunWith(MockitoJUnitRunner.class)
public class NationalSearchControllerTest extends WithApplication {

    @Mock
    private Search elasticSearch;

    @Test
    public void blankSearchTermReturnsAnEmptyArrayResult() {
        val request = new Http.RequestBuilder().method(GET).uri("/searchOffender/blank");
        val result = route(app, request);

        assertEquals(OK, result.status());
        assertEquals("[]", contentAsString(result));
    }

    @Test
    public void searchTermReturnsResults() {
        when(elasticSearch.search(any())).thenReturn(new OffenderSearchResult());
        val request = new Http.RequestBuilder().method(GET).uri("/searchOffender/smith");
        val result = route(app, request);

        assertEquals(OK, result.status());
        assertEquals("{\"offenders\":[],\"suggestions\":[]}", contentAsString(result));
    }

    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder().
            overrides(
                bind(Search.class).toInstance(elasticSearch)
            )
            .build();
    }

}