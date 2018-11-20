package injection;

import com.typesafe.config.Config;
import interfaces.PrisonerCategoryApi;
import play.inject.Injector;
import services.NomisElite2Api;
import services.stubs.StubPrisonerApi;

import javax.inject.Inject;
import javax.inject.Provider;

public class PrisonerCategoryApiProvider implements Provider<PrisonerCategoryApi> {

    private final String prisonerApiProvider;
    private Injector injector;

    @Inject
    public PrisonerCategoryApiProvider(Config configuration, Injector injector) {
        this.prisonerApiProvider = configuration.getString("prisoner.api.provider");
        this.injector = injector;
    }

    @Override
    public PrisonerCategoryApi get() {
        return "stub".equals(prisonerApiProvider) ?
                        injector.instanceOf(StubPrisonerApi.class) :
                        injector.instanceOf(NomisElite2Api.class);
    }
}
