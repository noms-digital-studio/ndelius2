package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.openqa.selenium.By.id;

public class OffenceDetailsPage extends FluentPage {
    private final SourcesOfInformationPage sourcesOfInformationPage;
    public OffenceDetailsPage(FluentControl control) {
        super(control);
        sourcesOfInformationPage = new SourcesOfInformationPage(control);
    }

    public OffenceDetailsPage navigateHere() {
        sourcesOfInformationPage.navigateHere().gotoNext();
        return this;
    }

    public OffenceDetailsPage gotoNext() {
        $(id("offenceSummary")).fill().with("Offence summary");
        $(id("mainOffence")).fill().with("Main offence");
        $(id("nextButton")).click();
        return this;
    }
}
