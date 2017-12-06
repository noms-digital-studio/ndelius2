package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.openqa.selenium.By.id;

public class OffenderDetailsPage extends FluentPage {
    private final StartPage startPage;
    public OffenderDetailsPage(FluentControl control) {
        super(control);
        startPage = new StartPage(control);
    }

    public OffenderDetailsPage navigateHere() {
        startPage.navigateHere().gotoNext();
        return this;
    }

    public OffenderDetailsPage gotoNext() {
        $(id("nextButton")).click();
        return this;
    }

}
