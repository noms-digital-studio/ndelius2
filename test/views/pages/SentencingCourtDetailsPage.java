package views.pages;

import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;
import play.test.TestBrowser;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class SentencingCourtDetailsPage {
    private final TestBrowser browser;

    public SentencingCourtDetailsPage(TestBrowser browser) {
        this.browser = browser;
    }

    public SentencingCourtDetailsPage navigateHere() {
        browser.goTo("/report/shortFormatPreSentenceReport?onBehalfOfUser=92Q036CvVIRT%2Fi428X3zpg%3D%3D&name=xylkFTVA6GXA1GRZZxZ4MA%3D%3D&localJusticeArea=EH5gq4HPVBLvqnZIG8zkYDK1zJo3nrGPNgKqHKceJUU%3D&dateOfHearing=igY1rhdHh6XNlTto%2BoNRSw%3D%3D&dateOfBirth=twqjuUftRY5xaB556mJb6A%3D%3D&court=eqyTlt9YxlqALprD4wyBD4bUUntAZzSTw4BTi%2BAN1jwwQb7aDEOUHrvD3Nc5CsmQ&crn=v5LH8B7tJKI7fEc9uM76SQ%3D%3D&age=RRioaTyIHLGnja2CBw8hqg%3D%3D&entityId=RRioaTyIHLGnja2CBw8hqg%3D%3D");
        browser.find(By.id("nextButton")).click();
        browser.find(By.id("nextButton")).click();
        return this;
    }

    public List<String> localJusticeAreas() {
        return browser.find(By.name("localJusticeArea")).
                find(By.tagName("option")).
                stream().
                map(FluentWebElement::value).
                filter(locale -> locale.contains("London")).
                collect(Collectors.toList());

    }
}
