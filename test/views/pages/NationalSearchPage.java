package views.pages;

import org.fluentlenium.core.FluentControl;
import org.fluentlenium.core.FluentPage;

public class NationalSearchPage extends FluentPage {

    public NationalSearchPage(FluentControl control) {
        super(control);
    }

    public NationalSearchPage navigateHere() {
        goTo("/nationalSearch");
        return this;
    }
}
