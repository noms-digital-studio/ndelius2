package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.openqa.selenium.By.id;

public class OffenceAnalysisPage extends FluentPage {
    private final OffenceDetailsPage offenceDetailsPage;
    public OffenceAnalysisPage(FluentControl control) {
        super(control);
        offenceDetailsPage = new OffenceDetailsPage(control);
    }

    public OffenceAnalysisPage navigateHere() {
        offenceDetailsPage.navigateHere().gotoNext();

        return this;
    }

    public OffenceAnalysisPage gotoNext() {
        $(id("offenceAnalysis")).fill().with("Offence analysis");
        $(id("nextButton")).click();
        return this;
    }
}
