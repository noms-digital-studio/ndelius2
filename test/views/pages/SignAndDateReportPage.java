package views.pages;

import org.fluentlenium.core.FluentPage;
import org.openqa.selenium.By;
import play.test.TestBrowser;


public class SignAndDateReportPage extends FluentPage {
    private final TestBrowser browser;

    public SignAndDateReportPage(TestBrowser browser) {
        this.browser = browser;
    }

    public SignAndDateReportPage navigateHere() {
        // goto start now
        browser.goTo("/report/shortFormatPreSentenceReport?onBehalfOfUser=92Q036CvVIRT%2Fi428X3zpg%3D%3D&name=xylkFTVA6GXA1GRZZxZ4MA%3D%3D&localJusticeArea=EH5gq4HPVBLvqnZIG8zkYDK1zJo3nrGPNgKqHKceJUU%3D&dateOfHearing=igY1rhdHh6XNlTto%2BoNRSw%3D%3D&dateOfBirth=twqjuUftRY5xaB556mJb6A%3D%3D&court=eqyTlt9YxlqALprD4wyBD4bUUntAZzSTw4BTi%2BAN1jwwQb7aDEOUHrvD3Nc5CsmQ&crn=v5LH8B7tJKI7fEc9uM76SQ%3D%3D&age=RRioaTyIHLGnja2CBw8hqg%3D%3D&entityId=RRioaTyIHLGnja2CBw8hqg%3D%3D");

        // goto offender details
        browser.find(By.id("nextButton")).click();

        // goto sentencing court details
        browser.find(By.id("nextButton")).click();

        // goto sources of information
        browser.find(By.id("nextButton")).click();

        // goto offence details
        browser.find(By.id("nextButton")).click();
        browser.find(By.id("offenceSummary")).write("Offence summary");
        browser.find(By.id("mainOffence")).write("Main offence");

        // goto offence analysis
        browser.find(By.id("nextButton")).click();
        browser.find(By.id("offenceAnalysis")).write("Offence analysis");

        // goto offender assessment
        browser.find(By.id("nextButton")).click();
        browser.find(By.id("offenderAssessment")).write("Offender assessment");

        // goto risk assessment
        browser.find(By.id("nextButton")).click();
        browser.find(By.id("likelihoodOfReOffending")).write("Likelihood Of ReOffending");
        browser.find(By.id("riskOfSeriousHarm")).write("Risk of Serious Harm");
        browser.find(By.id("previousSupervisionResponse_Good")).click();
        browser.find(By.id("additionalPreviousSupervision")).write("Additional previous supervision");

        // goto Conclusion
        browser.find(By.id("nextButton")).click();
        browser.find(By.id("proposal")).write("Proposal");

        // goto Summary
        browser.find(By.id("nextButton")).click();

        // goto sign and date report
        browser.find(By.id("nextButton")).click();

        return this;
    }

    public String getMainHeading() {
        return browser.find(By.tagName("h1")).text();
    }
}
