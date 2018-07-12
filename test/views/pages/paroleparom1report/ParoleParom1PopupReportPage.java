package views.pages.paroleparom1report;

import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.By;
import play.test.TestBrowser;

public class ParoleParom1PopupReportPage extends FluentPage {
    public ParoleParom1PopupReportPage(TestBrowser control) {
        super(control);
    }

    @Override
    public void isAt(Object... parameters) {
        control.await().until(driver -> driver.find(By.tagName("h1")).first().text().equals(parameters[0]));
    }

}
