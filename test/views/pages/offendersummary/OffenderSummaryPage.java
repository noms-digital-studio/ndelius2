package views.pages.offendersummary;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.val;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;
import play.test.TestBrowser;

import javax.inject.Inject;
import java.time.Instant;

import static views.pages.ParameterEncrypt.encrypt;

public class OffenderSummaryPage extends FluentPage {
    @Data
    @ToString
    @Builder(toBuilder = true)
    public static class RegistrationTableRow {
        private int rowNumber;
        private String type;
        private String statusWord;
        private String statusColour;
        private String description;
        private String date;
    }


    @Inject
    public OffenderSummaryPage(TestBrowser control) {
        super(control);
    }

    public OffenderSummaryPage navigateHere() {
        goTo(String.format("/offenderSummary?offenderId=%s&user=%s&t=%s",
                "12345",
                encrypt("john.smith"),
                encrypt(String.format("%d", Instant.now().toEpochMilli()))
        ));

        control.await().until($(By.className("qa-offender-identity")));

        return this;
    }

    public String getOffenderName() {
        return $(By.className("qa-offender-name")).text();
    }

    public String getOffenderDateOfBirth() {
        return $(By.className("qa-offender-date-of-birth")).text();
    }

    public String getOffenderCRN() {
        return $(By.className("qa-offender-crn")).text();
    }

    public String getOffenderImageUrl() {
        return $(By.className("offender-image")).attribute("src");
    }

    public boolean hasRegistrationTableWithRow(RegistrationTableRow registrationTableRow) {
        await().until($(".qa-offender-registrations")).size(1);

        val row = $(".qa-offender-registrations tbody tr").index(registrationTableRow.getRowNumber());

        return row.find(".moj-risk-tag").first().getElement().getCssValue("background-color").equals(colourToRGB(registrationTableRow.getStatusColour())) &&
                row.text().contains(String.format("%s %s %s %s", registrationTableRow.getType(), registrationTableRow.getStatusWord().toLowerCase(), registrationTableRow.getDescription(), registrationTableRow.getDate()));
    }

    private String colourToRGB(String statusColour) {
        // map the BDD colours to actual CSS colours presented on screen
        // this is preferable to mapping to css class names that are identified by 'low', 'medium' etc
        // a nicer way would be doing a complex difference calculation between these values and the formal
        // definition of white, amber etc - all a bit OTT for now allow css changes to break BDD
        switch (statusColour) {
            case "Red":
                return "rgba(177, 14, 30, 1)";
            case "Amber":
                return "rgba(255, 191, 71, 1)";
            case "Green":
                return "rgba(0, 100, 53, 1)";
            case "White":
                return "rgba(0, 0, 0, 0)";
        }

        return "";
    }

    public String getRegistrationTableText() {
        await().until($(".qa-offender-registrations")).size(1);
        return $(By.className("qa-offender-registrations")).text();
    }

    public void clickAccordion(String partialText) {
        await().until($(By.partialLinkText(partialText))).size(1);
        $(By.partialLinkText(partialText)).click();
    }

}