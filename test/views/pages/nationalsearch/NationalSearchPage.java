package views.pages.nationalsearch;

import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.By;
import play.test.TestBrowser;

import javax.inject.Inject;
import java.time.Instant;

import static org.openqa.selenium.By.xpath;
import static views.pages.ParameterEncrypt.encrypt;

public class NationalSearchPage extends FluentPage {


    @Inject
    public NationalSearchPage(TestBrowser control) {
        super(control);
    }

    public NationalSearchPage navigateHere() {
        goTo(String.format("/nationalSearch?&user=%s&t=%s",
                encrypt("john.smith"),
                encrypt(String.format("%d", Instant.now().toEpochMilli()))
        ));

        control.await().until($(By.cssSelector("#searchTerms")).first()).displayed();

        return this;
    }

    public boolean verifySearchInput() {
        return $(By.cssSelector("#searchTerms")).present();
    }

    public boolean hasLinkWithTest(String linkText) {
        return $(By.linkText(linkText)).present();
    }


    public void enterSearchPhrase(String searchTerm) {
        $(By.cssSelector("#searchTerms")).fill().withText(searchTerm);
        $(By.cssSelector("#searchTerms")).first().keyboard().sendKeys("\r");
    }

    public void searchNow() {
        $(By.cssSelector("#searchTerms")).write("\r");
    }

    public boolean searchResultsPresent() {
        await().until($(By.cssSelector(".qa-offender-details-row")).first()).present();
        return true;
    }

    public int countResultsDisplayed() {
        return $(By.cssSelector(".qa-offender-details-row")).size();
    }

    public String allResultsText() {
        return $(By.cssSelector("#offender-results")).text();
    }

    public String suggestionText() {
        return $(xpath("//p[span[text()='Did you mean']]")).text();
    }

    public String enteredSearchPhrase() {
        return $(By.cssSelector("#searchTerms")).value();
    }

    public boolean myProvidersFilterPresent() {
        return $(By.cssSelector("#filters-my-providers")).present();
    }

    public boolean otherProvidersFilterPresent() {
        return $(By.cssSelector("#filters-all-providers")).present();
    }
}
