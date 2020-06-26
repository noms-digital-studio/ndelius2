package injection;

import com.typesafe.config.Config;
import interfaces.PrisonerApi;
import play.inject.Injector;
import services.NomisCustodyApi;
import services.NomisElite2Api;
import services.stubs.StubPrisonerApi;

import javax.inject.Inject;
import javax.inject.Provider;

public class PrisonerApiProvider implements Provider<PrisonerApi> {

    private final String prisonerApiProvider;
    private Injector injector;

    @Inject
    public PrisonerApiProvider(Config configuration, Injector injector) {
        this.prisonerApiProvider = configuration.getString("prisoner.api.provider");
        this.injector = injector;
    }

    @Override
    public PrisonerApi get() {
        return "stub".equals(prisonerApiProvider) ?
                injector.instanceOf(StubPrisonerApi.class) :
                "elite".equals(prisonerApiProvider) ?
                        injector.instanceOf(NomisElite2Api.class) :
                        injector.instanceOf(NomisCustodyApi.class);
    }
}
