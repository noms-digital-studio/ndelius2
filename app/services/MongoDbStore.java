package services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import com.typesafe.config.Config;
import interfaces.AnalyticsStore;
import lombok.val;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.joda.time.DateTime;
import play.Logger;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;

public class MongoDbStore implements AnalyticsStore {

    private final MongoCollection<Document> events;
    private final MongoDatabase database;

    @Inject
    public MongoDbStore(Config configuration,
                        MongoClient mongoClient) {

        val databaseName = configuration.getString("analytics.mongo.database");
        val collectionName = configuration.getString("analytics.mongo.collection");

        events = mongoClient.getDatabase(databaseName).getCollection(collectionName);
        database = mongoClient.getDatabase(databaseName);
    }

    @Override
    public void recordEvent(Map<String, Object> data) {

        events.insertOne(new Document(data)).subscribe(
                success -> { },
                error -> Logger.error("Analytics Error", error)
        );
    }

    @Override
    public CompletableFuture<List<Map<String, Object>>> recentEvents(int limit) {

        val result = new CompletableFuture<List<Map<String, Object>>>();

        events.find().
                projection(new Document(ImmutableMap.of(
                        "_id", 0,
                        "dateTime", 1,
                        "pageNumber", 1,
                        "sessionId", 1
                ))).
                sort(new Document("$natural", -1)).
                limit(limit).
                toObservable().
                map(document -> document.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {

                    val value = entry.getValue();

                    return value.getClass().equals(Date.class) ? new DateTime(value).toString() : value;

                }))).
                toList().
                doOnError(result::completeExceptionally).
                subscribe(result::complete);

        return result;
    }

    @Override
    public CompletableFuture<Map<Integer, Long>> pageVisits() {

        val result = new CompletableFuture<Map<Integer, Long>>();

        Document match = new Document(
            ImmutableMap.of(
                "$match", new Document(
                    ImmutableMap.of(
                        "type", new Document(
                            "$exists", false
                        )
                ))
            ));

        Document sum = new Document(
            ImmutableMap.of(
                "$group", new Document(
                    ImmutableMap.of(
                        "_id", "$pageNumber",
                        "total", new Document(
                            ImmutableMap.of("$sum", 1l)
                        )
                    ))
            ));

        Document sort = new Document(ImmutableMap.of(
            "$sort", new Document(ImmutableMap.of(
                "_id", 1
            ))
        ));

        val group = ImmutableList.of(match, sum, sort);

        events.aggregate(group).
                toObservable().
                toList().
                map(documents -> documents.stream().collect(
                        Collectors.toMap(doc -> doc.getInteger("_id"), doc -> doc.getLong("total")))
                ).
                doOnError(result::completeExceptionally).
                subscribe(result::complete);

        return result;
    }

    @Override
    public CompletableFuture<Long> pageVisits(String eventType, LocalDateTime from) {
        val result = new CompletableFuture<Long>();

        events.count(and(eq("type", eventType), filterByDate(from))).
                doOnError(result::completeExceptionally).
                subscribe(result::complete);

        return result;
    }

    @Override
    public CompletableFuture<Long> uniquePageVisits(String eventType, LocalDateTime from) {
        val result = new CompletableFuture<Long>();

        events.distinct("username", String.class).
                filter(and(eq("type", eventType), filterByDate(from))).
                toObservable().
                toList().
                doOnError(result::completeExceptionally).
                subscribe((usernameList) -> result.complete((long) usernameList.size()));

        return result;
    }

    @Override
    public CompletableFuture<Boolean> isUp() {
        val result = new CompletableFuture<Boolean>();

         database.runCommand(new Document("dbStats", 1))
             .timeout(5000, TimeUnit.MILLISECONDS)
             .map(document -> document.get("ok").equals(1.0))
             .onErrorReturn(ignored -> result.complete(false))
             .subscribe(result::complete);

        return result;
    }

    private Bson filterByDate(LocalDateTime from) {
        return gte("dateTime", Date.from(from.atZone(ZoneId.systemDefault()).toInstant()));
    }

}
