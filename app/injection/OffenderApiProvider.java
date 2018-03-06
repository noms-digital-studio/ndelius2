package injection;

import com.typesafe.config.Config;
import interfaces.OffenderApi;
import play.inject.Injector;
import services.DeliusOffenderApi;
import services.fakes.FakeOffenderApi;

import javax.inject.Inject;
import javax.inject.Provider;

public class OffenderApiProvider implements Provider<OffenderApi> {

    private final boolean standAloneOperation;
    private Injector injector;

    @Inject
    public OffenderApiProvider(Config configuration, Injector injector) {
        this.standAloneOperation = configuration.getBoolean("standalone.operation");
        this.injector = injector;
    }

    @Override
    public OffenderApi get() {
        return standAloneOperation ? injector.instanceOf(FakeOffenderApi.class) : injector.instanceOf(DeliusOffenderApi.class);
    }
}
