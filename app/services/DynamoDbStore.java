package services;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.typesafe.config.Config;
import interfaces.AnalyticsStore;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.val;

public class DynamoDbStore implements AnalyticsStore {

    private final Table events;

    @Inject
    public DynamoDbStore(Config configuration,
                         DynamoDB dynamoClient) {

        val tableName = configuration.getString("analytics.dynamo.table");

        events = dynamoClient.getTable(tableName);
    }

    @Override
    public void recordEvent(Map<String, Object> data) {

        events.putItem(new Item().withMap("event", data));
    }

    @Override
    public CompletableFuture<List<Map<String, Object>>> recentEvents(int limit) {

        val result = new CompletableFuture<List<Map<String, Object>>>();
/*
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
*/
        return result;
    }

    @Override
    public CompletableFuture<Map<Integer, Integer>> pageVisits() {

        val result = new CompletableFuture<Map<Integer, Integer>>();
/*
        val group = ImmutableList.of(
                new Document(ImmutableMap.of(
                        "$group", new Document(ImmutableMap.of(
                                "_id", "$pageNumber",
                                "total", new Document(ImmutableMap.of(
                                        "$sum", 1
                                ))
                        ))
                )),
                new Document(ImmutableMap.of(
                        "$sort", new Document(ImmutableMap.of(
                                "_id", 1
                        ))
                ))
        );

        events.aggregate(group).
                toObservable().
                toList().
                map(documents -> documents.stream().collect(
                        Collectors.toMap(doc -> doc.getInteger("_id"), doc -> doc.getInteger("total")))
                ).
                doOnError(result::completeExceptionally).
                subscribe(result::complete);
*/
        return result;
    }
}
