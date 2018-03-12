package injection;

import com.typesafe.config.Config;
import interfaces.OffenderApi;
import play.inject.Injector;
import services.DeliusOffenderApi;
import services.fakes.FakeOffenderApi;

import javax.inject.Inject;
import javax.inject.Provider;

public class OffenderApiProvider implements Provider<OffenderApi> {

    private final boolean offenderApiStandAloneOperation;
    private Injector injector;

    @Inject
    public OffenderApiProvider(Config configuration, Injector injector) {
        this.offenderApiStandAloneOperation = configuration.getBoolean("offender.api.standalone.operation");
        this.injector = injector;
    }

    @Override
    public OffenderApi get() {
        return offenderApiStandAloneOperation ? injector.instanceOf(FakeOffenderApi.class) : injector.instanceOf(DeliusOffenderApi.class);
    }
}
