package injection;

import com.typesafe.config.Config;
import interfaces.OffenderApi;
import play.inject.Injector;
import services.DeliusOffenderApi;
import services.stubs.StubOffenderApi;

import javax.inject.Inject;
import javax.inject.Provider;

public class OffenderApiProvider implements Provider<OffenderApi> {

    private final String offenderApiProvider;
    private Injector injector;

    @Inject
    public OffenderApiProvider(Config configuration, Injector injector) {
        this.offenderApiProvider = configuration.getString("offender.api.provider");
        this.injector = injector;
    }

    @Override
    public OffenderApi get() {
        return "stub".equals(offenderApiProvider) ?
            injector.instanceOf(StubOffenderApi.class) : injector.instanceOf(DeliusOffenderApi.class);
    }
}
