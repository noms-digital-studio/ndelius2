package views.pages.shortformatpresentencereport;

import play.test.TestBrowser;
import views.pages.ReportPage;

import javax.inject.Inject;

public class ShortFormatPreSentencePopupReportPage extends ReportPage {

    @Inject
    public ShortFormatPreSentencePopupReportPage(TestBrowser control) {
        super(control);
    }

    public void jumpTo(Page page) {
        super.jumpTo(page.getPageNumber());
    }
}
