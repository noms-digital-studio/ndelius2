package views.pages.paroleparom1report;

import lombok.val;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;
import play.test.TestBrowser;

import javax.inject.Inject;

import java.util.Optional;

import static org.openqa.selenium.By.*;

public class ParoleParom1PopupReportPage extends FluentPage {
    @Inject
    public ParoleParom1PopupReportPage(TestBrowser control) {
        super(control);
    }

    @Override
    public void isAt(Object... parameters) {
        control.await().until(driver -> driver.find(By.tagName("h1")).first().text().equals(parameters[0]));
    }

    public void clickButton(String button) {
        $(By.xpath(String.format("//button[contains(text(),'%s')]", button))).click();
    }

    public void jumpTo(Page page) {
        String linkSelector = String.format("//a[@data-target='%s']", page.getPageNumber());
        control.await().until(driver -> driver.find(By.xpath(linkSelector)).size() == 1);
        $(By.xpath(linkSelector)).click();
    }

    public void fillClassicTextArea(String label, String text) {
        val fieldId = $(xpath(String.format("//label[span[text()='%s']]", label))).attribute("for");
        $(name(fieldId)).fill().with(text);
    }

    public void fillTextArea(String label, String text) {
        val fieldId = $(xpath(String.format("//label[span[text()='%s']]", label))).attribute("for");
        $(cssSelector(String.format("#%s .ql-editor", fieldId))).fill().with(text);
    }
    public void fillInput(String label, String text) {
        val fieldId = $(xpath(String.format("//label[span[text()='%s']]", label))).attribute("for");
        $(name(fieldId)).fill().with(text);
    }

    public void fillInputWithId(String fieldId, String text) {
        $(id(fieldId)).fill().with(text);
    }

    public void fillInputInSectionWithLegend(String legend, String label, String text) {
        val fieldId = findSectionFromLegend(legend).find(xpath(String.format(".//label[contains(., '%s')]", label))).attribute("for");
        $(id(fieldId)).fill().with(text);
    }
    public String fieldNameFromLabel(String label) {
        val fieldId = $(xpath(String.format("//label[span[text()='%s']]", label))).attribute("for");
        return Optional.ofNullable(fieldId).orElseGet(() ->  Optional.ofNullable(dateGroupFieldIdFromLegend(label)).orElseGet(() -> radioFieldNameFromLegend(label)));
    }

    private String radioFieldNameFromLegend(String legend) {
        // find any radio input within section with legend - we just need the name of form field name
        return findSectionFromLegend(legend).find(By.cssSelector("input[type='radio']")).attribute("name");
    }
    private String dateGroupFieldIdFromLegend(String legend) {
        // find any radio input within section with legend - we just need the name of form field name
        return findSectionFromLegend(legend).find(By.cssSelector(".govuk-date-input")).attribute("id");
    }

    public void clickElementWithId(String id) {
        $(id(id)).first().click();
    }

    public void clickRadioButtonWithLabelWithinLegend(String label, String legend) {
        val fieldId = fieldNameFromLabelWithLegend(label, legend);
        $(id(fieldId)).first().click();
    }

    private String fieldNameFromLabelWithLegend(String label, String legend) {
        return findSectionFromLegend(legend)
                .find(xpath(String.format(".//label[contains(.,'%s')]", label)))
                .attribute("for");
    }

    private FluentWebElement findSectionFromLegend(String legend) {
        return $(xpath(String.format("//fieldset[legend[contains(.,'%s')]]", legend))).last();
    }

    public String errorMessage(String name) {
        return $(xpath(String.format("//a[@href='#%s-error']", name))).text();
    }

    public void clickCheckboxWithLabel(String label) {
        val fieldId = $(xpath(String.format("//label[span[text()='%s']]", label))).attribute("for");
        $(id(fieldId)).first().click();
    }

    public void clickSpanWithSiblingLabel(String text, String label) {
        val parent = $(xpath(String.format("//label[span[text()='%s']]", label))).first().find(By.xpath("./.."));
        parent.find(xpath(String.format(".//span[contains(.,'%s')]", text))).click();
    }

    public boolean whatToIncludeContentVisibleWithSiblingLabel(String label) {
        val parent = $(xpath(String.format("//label[span[text()='%s']]", label))).first().find(By.xpath("./.."));
        return parent.find(By.cssSelector(".govuk-details__text")).first().displayed();

    }

    public void clickLink(String linkText) {
        $(By.linkText(linkText)).click();
    }
}
