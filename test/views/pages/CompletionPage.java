package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

public class CompletionPage extends FluentPage {
    private final SignAndDateReportPage signAndDateReportPage;

    public CompletionPage(FluentControl control) {
        super(control);
        signAndDateReportPage = new SignAndDateReportPage(control);
    }

    @Override
    public void isAt() {
        assertThat(window().title()).contains("Report saved - Short Format Pre Sentence Report");
    }

    public CompletionPage navigateHere() {
        signAndDateReportPage.navigateHere().gotoNext();
        return this;
    }

    public String getAllText() {
        return $(By.tagName("body")).text().trim();
    }

    public String getAllContent() {
        return $(By.tagName("body")).textContent().trim();
    }
}
