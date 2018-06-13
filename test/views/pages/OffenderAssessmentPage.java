package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import java.util.List;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.xpath;

public class OffenderAssessmentPage extends FluentPage {
    private final OffenceAnalysisPage offenceAnalysisPage;
    public OffenderAssessmentPage(FluentControl control) {
        super(control);
        offenceAnalysisPage = new OffenceAnalysisPage(control);
    }

    public OffenderAssessmentPage navigateHere() {
        offenceAnalysisPage.navigateHere().gotoNext();
        return this;
    }

    public OffenderAssessmentPage gotoNext() {
        tick("Relationships");
        $(id("experienceTrauma_no")).click();
        $(id("caringResponsibilities_no")).click();
        $(id("nextButton")).click();
        return this;
    }


    public OffenderAssessmentPage attemptNext() {
        $(id("nextButton")).click();
        return this;
    }

    public void jumpToPage(String pageNumber) {
        control.executeScript(String.format("$('#jumpNumber').val('%s'); $('form').submit()", pageNumber));
    }


    public List<String> issues() {
        return $(cssSelector("label[for*='issue'] span")).texts();
    }

    public OffenderAssessmentPage tick(String optionLabel) {
        $(xpath(String.format("//label[span[text()='%s']]", optionLabel))).click();
        return this;
    }

    public OffenderAssessmentPage fillDetailsWith(String optionLabel, String text) {
        final String checkboxId = checkBoxIdFromLabel(optionLabel);
        final String associatedDetailsId = checkboxId+"Details";
        $(id(associatedDetailsId)).fill().with(text);
        return this;
    }

    public boolean isTicked(String optionLabel) {
        final String checkboxId = checkBoxIdFromLabel(optionLabel);
        return $(id(checkboxId)).attribute("checked") != null;
    }

    private String checkBoxIdFromLabel(String optionLabel) {
        return $(xpath(String.format("//label[span[text()='%s']]", optionLabel))).attribute("for");
    }

    public String associatedDetailsFor(String optionLabel) {
        final String checkboxId = checkBoxIdFromLabel(optionLabel);
        final String associatedDetailsId = checkboxId+"Details";
        return $(id(associatedDetailsId)).text();
    }

    public OffenderAssessmentPage yesWithDetailsFor(String field, String details) {
        $(id(String.format("%s_yes", field))).click();
        $(id(String.format("%sDetails", field))).fill().with(details);
        return this;
    }

    public int countErrors(String errorMessage) {
        return $(xpath(String.format("//span[@class='error-message' and text()='%s']", errorMessage))).count();
    }

    public void saveAsDraft() {
        $(id("exitLink")).click();
    }
}
