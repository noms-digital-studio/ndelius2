package views.pages.paroleparom1report;

import org.fluentlenium.core.FluentPage;
import play.test.TestBrowser;

import javax.inject.Inject;

import static org.openqa.selenium.By.id;

public class LandingPage extends FluentPage {
    @Inject
    public LandingPage(TestBrowser control) {
        super(control);
    }

    public LandingPage navigateHere() {
        goTo("/report/paroleParom1Report?onBehalfOfUser=92Q036CvVIRT%2Fi428X3zpg%3D%3D&crn=v5LH8B7tJKI7fEc9uM76SQ%3D%3D&entityId=RRioaTyIHLGnja2CBw8hqg%3D%3D&user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D");
        return this;
    }

    public LandingPage gotoNext() {
        $(id("nextButton")).click();
        window().switchTo("reportpopup");
        return this;
    }
}
