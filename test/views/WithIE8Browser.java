package views;

import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import play.test.Helpers;
import play.test.TestBrowser;
import play.test.WithBrowser;

import static com.gargoylesoftware.htmlunit.BrowserVersion.INTERNET_EXPLORER;

public class WithIE8Browser extends WithBrowser {
    @Override
    protected TestBrowser provideBrowser(int port) {
        INTERNET_EXPLORER.setUserAgent("Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E)");
        INTERNET_EXPLORER.setBrowserVersion(8);

        return Helpers.testBrowser(new HtmlUnitDriver(INTERNET_EXPLORER, true), port);
    }
}
