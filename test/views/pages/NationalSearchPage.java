package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

import static org.openqa.selenium.By.name;

public class NationalSearchPage extends FluentPage {

    public NationalSearchPage(FluentControl control) {
        super(control);
    }

    public NationalSearchPage navigateHere() {
        goTo("/nationalSearch?user=lJqZBRO%2F1B0XeiD2PhQtJg%3D%3D&t=T2DufYh%2B%2F%2F64Ub6iNtHDGg%3D%3D");
        return this;
    }

    public boolean hasSearchBox() {
        return $(name("searchTerms")).present();
    }
}
