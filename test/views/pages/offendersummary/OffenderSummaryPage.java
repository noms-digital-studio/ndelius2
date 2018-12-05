package views.pages.offendersummary;

import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.By;
import play.test.TestBrowser;

import javax.inject.Inject;
import java.time.Instant;

import static views.pages.ParameterEncrypt.encrypt;

public class OffenderSummaryPage extends FluentPage {
    @Inject
    public OffenderSummaryPage(TestBrowser control) {
        super(control);
    }

    public OffenderSummaryPage navigateHere() {
        goTo(String.format("/offenderSummary?offenderId=%s&user=%s&t=%s",
                "12345",
                encrypt("john.smith"),
                encrypt(String.format("%d", Instant.now().toEpochMilli()))
        ));

        control.await().until($(By.className("qa-offender-identity")));

        return this;
    }

    public String getOffenderName() {
        return $(By.className("qa-offender-name")).text();
    }

    public String getOffenderDateOfBirth() {
        return $(By.className("qa-offender-date-of-birth")).text();
    }

    public String getOffenderCRN() {
        return $(By.className("qa-offender-crn")).text();
    }

    public String getOffenderImageUrl() {
        return $(By.className("offender-image")).attribute("src");
    }
}
