package injection;

import com.typesafe.config.Config;
import interfaces.AnalyticsStore;
import play.inject.Injector;
import services.InMemoryAnalyticsStore;
import services.MongoDbStore;

import javax.inject.Inject;
import javax.inject.Provider;

public class AnalyticsStoreProvider implements Provider<AnalyticsStore> {

    private final boolean standAloneOperation;
    private Injector injector;

    @Inject
    public AnalyticsStoreProvider(Config configuration, Injector injector) {
        this.standAloneOperation = configuration.getBoolean("standalone.operation");
        this.injector = injector;
    }

    @Override
    public AnalyticsStore get() {
        return standAloneOperation ? injector.instanceOf(InMemoryAnalyticsStore.class) : injector.instanceOf(MongoDbStore.class);
    }
}
