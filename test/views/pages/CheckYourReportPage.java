package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.xpath;

public class CheckYourReportPage extends FluentPage {
    private final SourcesOfInformationPage sourcesOfInformationPage;
    public static final String PAGE_NUMBER = "10";


    public CheckYourReportPage(FluentControl control) {
        super(control);
        sourcesOfInformationPage = new SourcesOfInformationPage(control);
    }

    public CheckYourReportPage navigateHere() {
        sourcesOfInformationPage.navigateHere().gotoNext();
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

    public void clickOffenderDetailsLink() {
        $(By.linkText("Offender details")).click();
    }
}