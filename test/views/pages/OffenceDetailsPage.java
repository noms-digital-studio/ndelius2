package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.openqa.selenium.By.id;

public class OffenceDetailsPage extends FluentPage {
    private final SentencingCourtDetailsPage sentencingCourtDetailsPage;
    public OffenceDetailsPage(FluentControl control) {
        super(control);
        sentencingCourtDetailsPage = new SentencingCourtDetailsPage(control);
    }

    public OffenceDetailsPage navigateHere() {
        sentencingCourtDetailsPage.navigateHere().gotoNext();
        return this;
    }

    public OffenceDetailsPage gotoNext() {
        $(id("offenceSummary")).fill().with("Offence summary");
        $(id("mainOffence")).fill().with("Main offence");
        $(id("nextButton")).click();
        return this;
    }
}
