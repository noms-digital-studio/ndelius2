package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.openqa.selenium.By.id;

public class SentencingCourtDetailsPage extends FluentPage {
    private OffenderDetailsPage offenderDetailsPage;

    public SentencingCourtDetailsPage(FluentControl control) {
        super(control);
        offenderDetailsPage = new OffenderDetailsPage(control);
    }

    public SentencingCourtDetailsPage navigateHere() {
        offenderDetailsPage.navigateHere().gotoNext();
        return this;
    }

    public SentencingCourtDetailsPage gotoNext() {
        $(id("nextButton")).click();
        return this;
    }

}
