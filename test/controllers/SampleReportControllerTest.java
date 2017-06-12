package controllers;

import org.junit.Test;
import play.Application;
import play.filters.csrf.*;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.RequestBuilder;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

//@TODO: CONVERT TO GROOVY/SPOCK TESTS? - sbt runner for groovy code??

public class SampleReportControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    protected RequestBuilder addCsrfToken(RequestBuilder requestBuilder) {
        final CSRFFilter csrfFilter = app.injector().instanceOf(CSRFFilter.class);
        final CSRFConfig csrfConfig = app.injector().instanceOf(CSRFConfigProvider.class).get();
        final String token = csrfFilter.tokenProvider().generateToken();

        requestBuilder.tag(CSRF.Token$.MODULE$.NameRequestTag(), csrfConfig.tokenName());
        requestBuilder.tag(CSRF.Token$.MODULE$.RequestTag(), token);
        requestBuilder.header(csrfConfig.headerName(), token);

        return requestBuilder;
    }

    @Test
    public void testReportGet() {
        RequestBuilder request = new RequestBuilder()
                .method(GET)
                .uri("/sampleReport");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }


    @Test
    public void testReportPost() {
        HashMap<String, String> formData = new HashMap<>();
        formData.put("salutation", "Mr");
        formData.put("forename", "John");
        formData.put("surname", "Smith");
        formData.put("pageNumber", "1");
        RequestBuilder request = addCsrfToken(new RequestBuilder()
                .header("Host", "localhost")
                .method(POST)
                .bodyForm(formData)
                .uri("/sampleReport"));

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }
}

//@TODO: Tests of form wizard pages and injected config. Do a second sample form.
