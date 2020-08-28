package injection;

import com.typesafe.config.Config;
import interfaces.OffenderSearch;
import play.inject.Injector;
import services.ElasticOffenderSearch;
import services.ProbationOffenderSearch;

import javax.inject.Inject;
import javax.inject.Provider;

public class OffenderSearchProvider implements Provider<OffenderSearch> {

    private final String offenderSearchProvider;
    private final Injector injector;

    @Inject
    public OffenderSearchProvider(Config configuration, Injector injector) {
        this.offenderSearchProvider = configuration.getString("offender.search.provider");
        this.injector = injector;
    }

    @Override
    public OffenderSearch get() {
        return "probation-offender-search".equals(offenderSearchProvider) ?
                injector.instanceOf(ProbationOffenderSearch.class) :
                injector.instanceOf(ElasticOffenderSearch.class);
    }
}
