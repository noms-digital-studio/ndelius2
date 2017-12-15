package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.openqa.selenium.By.id;


public class SignAndDateReportPage extends FluentPage {
    private final CheckYourReportPage checkYourReportPage;

    public SignAndDateReportPage(FluentControl control) {
        super(control);
        checkYourReportPage = new CheckYourReportPage(control);
    }

    public SignAndDateReportPage navigateHere() {
        checkYourReportPage.navigateHere().gotoNext();
        return this;
    }

    public SignAndDateReportPage gotoNext() {
        $(id("reportAuthor")).fill().with("Report Author");
        $(id("office")).fill().with("Office");
        $(id("courtOfficePhoneNumber")).fill().with("0114 555 5555");
        $(id("counterSignature")).fill().with("Counter Signature");
        $(id("nextButton")).click();
        return this;
    }

    public boolean hasCounterSignatureField() {
        return $(id("counterSignature")).present();
    }

    public boolean hasCourtOfficePhoneNumberField() {
        return $(id("courtOfficePhoneNumber")).present();
    }

    public boolean hasStartDateField() {
        return $(id("startDate")).present();
    }

    public boolean isStartDateFieldReadonly() {
        return $(id("startDate")).attribute("type").equals("hidden");
    }

    public boolean hasReportAuthorField() {
        return $(id("reportAuthor")).present();
    }

    public String getStartDate() {
        return $(id("value_startDate")).text();
    }

    public String getNextButtonText() {
        return $(id("nextButton")).value();
    }
}
