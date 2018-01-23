package views;

import org.junit.Before;
import org.junit.Test;
import play.test.WithBrowser;
import views.pages.NationalSearchPage;

// This test ensures that Module includes all the necessary dependencies for the application to start
public class ApplicationWebTest extends WithBrowser {
    private NationalSearchPage nationalSearchPage;

    @Before
    public void before() {
        nationalSearchPage = new NationalSearchPage(browser);
    }

    @Test
    public void signingReportNavigatesToCompletionPage() {
        nationalSearchPage.navigateHere().isAt();
    }

}
