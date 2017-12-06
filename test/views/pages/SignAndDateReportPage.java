package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.By;

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

}
