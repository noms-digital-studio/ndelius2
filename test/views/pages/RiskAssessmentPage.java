package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.openqa.selenium.By.id;

public class RiskAssessmentPage extends FluentPage {
    private final OffenderAssessmentPage offenderAssessmentPage;
    public RiskAssessmentPage(FluentControl control) {
        super(control);
        offenderAssessmentPage = new OffenderAssessmentPage(control);
    }

    public RiskAssessmentPage navigateHere() {
        offenderAssessmentPage.navigateHere().gotoNext();

        return this;
    }

    public RiskAssessmentPage gotoNext() {
        $(id("likelihoodOfReOffending")).fill().with("Likelihood Of ReOffending");
        $(id("riskOfSeriousHarm")).fill().with("Risk of Serious Harm");
        $(id("previousSupervisionResponse_Good")).click();
        $(id("additionalPreviousSupervision")).fill().with("Additional previous supervision");
        $(id("nextButton")).click();
        return this;
    }
}
