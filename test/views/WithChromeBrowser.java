package views;

import play.test.TestBrowser;
import play.test.WithBrowser;

public class WithChromeBrowser extends WithBrowser {
    @Override
    protected TestBrowser provideBrowser(int port) {
        return ChromeTestBrowser.create(port);
    }
}
