package views.pages.paroleparom1report;

import lombok.val;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;
import play.test.TestBrowser;

import javax.inject.Inject;

import java.util.Optional;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.name;
import static org.openqa.selenium.By.xpath;

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

    public void fillTextArea(String label, String text) {
        val fieldId = $(xpath(String.format("//label[span[text()='%s']]", label))).attribute("for");
        $(cssSelector(String.format("#%s .ql-editor", fieldId))).fill().with(text);
    }
    public void fillInput(String label, String text) {
        val fieldId = $(xpath(String.format("//label[span[text()='%s']]", label))).attribute("for");
        $(name(fieldId)).fill().with(text);
    }
    public String fieldNameFromLabel(String label) {
        val fieldId = $(xpath(String.format("//label[span[text()='%s']]", label))).attribute("for");
        return Optional.ofNullable(fieldId).orElseGet(() -> radioFieldNameFromLegend(label));
    }

    private String radioFieldNameFromLegend(String legend) {
        // find any radio input within section with legend - we just need the name of form field name
        return findSectionFromLegend(legend).find(By.cssSelector("input[type='radio']")).attribute("name");
    }

    public void clickRadioButtonWithLabelWithinLegend(String label, String legend) {
        val fieldId = fieldNameFromLabelWithLegend(label, legend);
        $(By.id(fieldId)).first().click();
    }

    private String fieldNameFromLabelWithLegend(String label, String legend) {
        return findSectionFromLegend(legend)
                .find(xpath(String.format(".//label[contains(.,'%s')]", label)))
                .attribute("for");
    }

    private FluentWebElement findSectionFromLegend(String legend) {
        return $(xpath(String.format("//fieldset[legend[contains(.,'%s')]]", legend))).first();
    }

    public String errorMessage(String name) {
        return $(xpath(String.format("//a[@href='#%s-error']", name))).text();
    }
}
