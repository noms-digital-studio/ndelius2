package views.pages.paroleparom1report;

import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import play.test.TestBrowser;

import javax.inject.Inject;
import java.time.Instant;

import static views.pages.ParameterEncrypt.encrypt;

public class LandingPage extends FluentPage {
    @Inject
    public LandingPage(TestBrowser control) {
        super(control);
    }

    public LandingPage navigateHere() {
        return navigateHereMalePrisoner();
    }

    public LandingPage navigateHereForFemale() {
        return navigateHereFemalePrisoner();
    }

    public LandingPage navigateHereMalePrisoner() {
        goTo(String.format("/report/paroleParom1Report?onBehalfOfUser=%s&user=%s&t=%s&crn=%s&entityId=%s",
                encrypt("Smith,John"),
                encrypt("john.smith"),
                encrypt(String.format("%d", Instant.now().toEpochMilli())),
                encrypt("X12345"),
                encrypt("12345")
        ));
        return this;
    }

    public LandingPage navigateHereFemalePrisoner() {
        goTo(String.format("/report/paroleParom1Report?onBehalfOfUser=%s&user=%s&t=%s&crn=%s&entityId=%s",
                encrypt("Smith,Jane"),
                encrypt("jane.smith"),
                encrypt(String.format("%d", Instant.now().toEpochMilli())),
                encrypt("X54321"),
                encrypt("54332")
        ));
        return this;
    }

    public LandingPage navigateWithExistingReport(String documentId) {
        goTo(String.format("/report/paroleParom1Report?documentId=%s&onBehalfOfUser=%s&user=%s&t=%s",
                encrypt(documentId),
                encrypt("Smith,John"),
                encrypt("john.smith"),
                encrypt(String.format("%d", Instant.now().toEpochMilli()))
        ));
        return this;
    }


    public LandingPage clickButton(String button) {
        $(By.xpath(String.format("//button[contains(text(),'%s')]", button))).click();
        window().switchTo("reportpopup");
        getDriver().manage().window().setSize(new Dimension(830, 3000));

        return this;
    }

    public void next() {
        clickButton("Start now");
    }

    public String lastUpdatedText() {
        return $(By.id("lastUpdated")).text();
    }

    public boolean verifyHasImage() {
       return $(By.tagName("img")).present();
    }

    public String fullNameText() {
        return $(By.className("qa-fullname")).text();
    }

    public String mainWarningMessage() {
        return $(By.className("qa-warning-message")).text();
    }
}
