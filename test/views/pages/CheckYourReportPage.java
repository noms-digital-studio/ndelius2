package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.xpath;

public class CheckYourReportPage extends FluentPage {
    private final ConclusionPage conclusionPage;
    public static final String PAGE_NUMBER = "10";


    public CheckYourReportPage(FluentControl control) {
        super(control);
        conclusionPage = new ConclusionPage(control);
    }

    public CheckYourReportPage navigateHere() {
        conclusionPage.navigateHere().gotoNext();
        return this;
    }

    public CheckYourReportPage gotoNext() {
        $(id("nextButton")).click();
        return this;
    }

    public void isAt() {
        assertThat(window().title()).contains("Check your report - Short Format Pre Sentence Report");
    }

    public String statusForOffenderAssessment() {
        return $(xpath("//tr[.//a[text()='Offender assessment']]//strong")).text();
    }
}