package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.openqa.selenium.By.id;

public class CheckYourReportPage extends FluentPage {
    private final ConclusionPage conclusionPage;

    public CheckYourReportPage(FluentControl control) {
        super(control);
        conclusionPage = new ConclusionPage(control);
    }

    public CheckYourReportPage navigateHere() {
        conclusionPage.navigateHere().gotoNext();
        return this;
    }

    public CheckYourReportPage gotoNext() {
        $(id("nextButton")).click();
        return this;
    }
}
