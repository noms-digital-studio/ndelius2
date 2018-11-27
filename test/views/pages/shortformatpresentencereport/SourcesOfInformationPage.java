package views.pages.shortformatpresentencereport;

import play.test.TestBrowser;

import javax.inject.Inject;
import java.util.List;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.xpath;

public class SourcesOfInformationPage extends ShortFormatPreSentencePopupReportPage {
    private final OffenderDetailsPage offenderDetailsPage;

    @Inject
    public SourcesOfInformationPage(OffenderDetailsPage offenderDetailsPage, TestBrowser control) {
        super(control);
        this.offenderDetailsPage = offenderDetailsPage;
    }

    public SourcesOfInformationPage navigateHere() {
        offenderDetailsPage.navigateHere();
        jumpTo(Page.SOURCES_OF_INFORMATION);
        return this;
    }

    public SourcesOfInformationPage gotoNext() {
        $(id("nextButton")).click();
        return this;
    }

    public List<String> sourcesOfInformation() {
        return $(cssSelector("label[for*='Source'] span")).texts();
    }

    public boolean isTicked(String informationSourceLabel) {
        final String checkboxId = checkBoxIdFromLabel(informationSourceLabel);
        return $(id(checkboxId)).attribute("checked") != null;    }

    public String otherInformationDetails() {
        return $(id("otherInformationDetails")).text();
    }

    public void attemptNext() {
        $(id("nextButton")).click();
    }

    public SourcesOfInformationPage fillOtherDetailsWith(String details) {
        $(id("otherInformationDetails")).fill().with(details);
        return this;
    }

    public SourcesOfInformationPage tick(String informationSourceLabel) {
        $(xpath(String.format("//label[span[contains(.,'%s')]]", informationSourceLabel))).click();
        return this;
    }

    private String checkBoxIdFromLabel(String optionLabel) {
        return $(xpath(String.format("//label[span[contains(.,'%s')]]", optionLabel))).attribute("for");
    }
}
