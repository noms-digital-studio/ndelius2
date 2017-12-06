package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;

import java.util.List;
import java.util.stream.Collectors;

import static org.openqa.selenium.By.id;

public class SentencingCourtDetailsPage extends FluentPage {
    private OffenderDetailsPage offenderDetailsPage;

    public SentencingCourtDetailsPage(FluentControl control) {
        super(control);
        offenderDetailsPage = new OffenderDetailsPage(control);
    }

    public SentencingCourtDetailsPage navigateHere() {
        offenderDetailsPage.navigateHere().gotoNext();
        return this;
    }

    public SentencingCourtDetailsPage gotoNext() {
        $(id("nextButton")).click();
        return this;
    }

    public List<String> localJusticeAreas() {
        return $(By.name("localJusticeArea")).
                $(By.tagName("option")).
                stream().
                map(FluentWebElement::value).
                filter(locale -> locale.contains("London")).
                collect(Collectors.toList());

    }
}
