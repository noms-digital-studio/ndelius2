package injection;

import com.typesafe.config.Config;
import interfaces.DocumentStore;
import play.inject.Injector;
import services.AlfrescoStore;
import services.stubs.MongoDocumentStore;

import javax.inject.Inject;
import javax.inject.Provider;

public class DocumentStoreProvider implements Provider<DocumentStore> {

    private final String storeProvider;
    private Injector injector;

    @Inject
    public DocumentStoreProvider(Config configuration, Injector injector) {
        this.storeProvider = configuration.getString("store.provider");
        this.injector = injector;
    }

    @Override
    public DocumentStore get() {
        return "mongo".equals(storeProvider) ?
            injector.instanceOf(MongoDocumentStore.class) : injector.instanceOf(AlfrescoStore.class);
    }

}
