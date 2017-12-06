package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.openqa.selenium.By.id;

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
        $(id("offenderAssessment")).fill().with("Offender assessment");
        $(id("nextButton")).click();
        return this;
    }
}
