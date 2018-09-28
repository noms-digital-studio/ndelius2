package injection;

import com.typesafe.config.Config;
import interfaces.PrisonerApiToken;
import play.inject.Injector;
import services.JwtNomisPrisonerApi;
import services.NomisCustodyAuthenticationApi;

import javax.inject.Inject;
import javax.inject.Provider;

public class PrisonerApiTokenProvider implements Provider<PrisonerApiToken> {

    private final String prisonerApiProvider;
    private Injector injector;

    @Inject
    public PrisonerApiTokenProvider(Config configuration, Injector injector) {
        this.prisonerApiProvider = configuration.getString("prisoner.api.provider");
        this.injector = injector;
    }

    @Override
    public PrisonerApiToken get() {
        return "legacy".equals(prisonerApiProvider) ?
            injector.instanceOf(JwtNomisPrisonerApi.class) : injector.instanceOf(NomisCustodyAuthenticationApi.class);
    }
}
