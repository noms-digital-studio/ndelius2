package services.stubs;

import interfaces.PrisonerApiToken;
import org.apache.commons.lang3.NotImplementedException;

public class StubPrisonerApiToken implements PrisonerApiToken {
    @Override
    public String get() {
        throw new NotImplementedException("this is just a lowly stub");
    }
}
