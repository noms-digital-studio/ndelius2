package services;

import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoCollection;
import com.typesafe.config.Config;
import interfaces.AnalyticsStore;
import javax.inject.Inject;
import java.util.Map;
import lombok.val;
import org.bson.Document;

public class MongoDbStore implements AnalyticsStore {

    private final MongoCollection<Document> events;

    @Inject
    public MongoDbStore(Config configuration,
                        MongoClient mongoClient) {

        val databaseName = configuration.getString("analytics.mongo.database");
        val collectionName = configuration.getString("analytics.mongo.collection");

        events = mongoClient.getDatabase(databaseName).getCollection(collectionName);
    }

    @Override
    public void recordEvent(Map<String, Object> data) {

        events.insertOne(new Document(data));
    }
}
