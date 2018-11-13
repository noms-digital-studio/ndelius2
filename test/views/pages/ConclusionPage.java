package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.openqa.selenium.By.id;

public class ConclusionPage extends FluentPage {
    private final RiskAssessmentPage riskAssessmentPage;

    public ConclusionPage(FluentControl control) {
        super(control);
        riskAssessmentPage = new RiskAssessmentPage(control);
    }

    public ConclusionPage navigateHere() {
        riskAssessmentPage.navigateHere().gotoNext();
        return this;
    }

    public ConclusionPage gotoNext() {
        $(id("proposal")).fill().with("Proposal");
        $(id("nextButton")).click();
        return this;
    }

}
