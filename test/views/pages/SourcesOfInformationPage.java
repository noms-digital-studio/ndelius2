package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.openqa.selenium.By.id;

public class SourcesOfInformationPage extends FluentPage {
    private final SentencingCourtDetailsPage sentencingCourtDetailsPage;
    public SourcesOfInformationPage(FluentControl control) {
        super(control);
        sentencingCourtDetailsPage = new SentencingCourtDetailsPage(control);
    }

    public SourcesOfInformationPage navigateHere() {
        sentencingCourtDetailsPage.navigateHere().gotoNext();
        return this;
    }

    public SourcesOfInformationPage gotoNext() {
        $(id("nextButton")).click();
        return this;
    }
}
