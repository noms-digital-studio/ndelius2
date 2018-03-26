package controllers;

import helpers.Encryption;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.test.Helpers;
import play.test.WithApplication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

@RunWith(MockitoJUnitRunner.class)
public class NationalSearchController_MaintenanceMode_Test extends WithApplication {

    @Test
    public void maintenancePageRendered() throws UnsupportedEncodingException {
        val result = route(app, buildIndexPageRequest());

        assertThat(result.status()).isEqualTo(OK);
        assertThat(Helpers.contentAsString(result)).contains("maintenance-mode");
    }


    private Http.RequestBuilder buildIndexPageRequest() throws UnsupportedEncodingException {
        val encryptedUser = URLEncoder.encode(Encryption.encrypt("roger.bobby", "ThisIsASecretKey"), "UTF-8");
        val encryptedTime = URLEncoder.encode(Encryption.encrypt(String.valueOf(Instant.now().toEpochMilli()), "ThisIsASecretKey"), "UTF-8");

        return new Http.RequestBuilder().method(GET).uri(String.format("/nationalSearch?user=%s&t=%s", encryptedUser, encryptedTime));
    }


    @Override
    protected Application provideApplication() {

        return new GuiceApplicationBuilder()
            .configure("maintenance.offender.search", true)
            .build();
    }
}