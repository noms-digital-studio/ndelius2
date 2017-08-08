package injection;

import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.typesafe.config.Config;
import javax.inject.Inject;
import javax.inject.Provider;

public class MongoClientProvider implements Provider<MongoClient> {

    private final String mongoDbUrl;

    @Inject
    public MongoClientProvider(Config configuration) {

        mongoDbUrl = configuration.getString("analytics.mongo.connection");
    }

    @Override
    public MongoClient get() {

        return MongoClients.create(mongoDbUrl);
    }
}
