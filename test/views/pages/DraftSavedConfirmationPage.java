package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.By.id;

public class DraftSavedConfirmationPage extends FluentPage {
    private final StartPage startPage;
    public DraftSavedConfirmationPage(FluentControl control) {
        super(control);
        startPage = new StartPage(control);
    }

    public DraftSavedConfirmationPage navigateHere() {
        startPage.navigateHere().gotoNext();
        return saveAsDraft();
    }

    public DraftSavedConfirmationPage gotoNext() {
        $(id("nextButton")).click();
        return this;
    }

    private DraftSavedConfirmationPage saveAsDraft() {
        $(id("exitLink")).click();
        return this;
    }

    public void isAt() {
        assertThat(window().title()).contains("Draft report saved - Short Format Pre Sentence Report");
    }


    public void updateReport() {
        $(By.cssSelector("#edit-pdf a")).click();
    }
}
