package services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import helpers.IterableScan;
import interfaces.AnalyticsStore;
import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.val;
import org.joda.time.DateTime;
import play.Logger;

public class DynamoDbStore implements AnalyticsStore {

    private final AmazonDynamoDB amazon;
    private final DynamoDB dynamoClient;

    @Inject
    public DynamoDbStore(AmazonDynamoDB amazon,
                         DynamoDB dynamoClient) {

        this.amazon = amazon;
        this.dynamoClient = dynamoClient;
    }

    @Override
    public void recordEvent(Map<String, Object> data) {

        final Function<String, KeyAttribute> keyAttribute = key -> {

            val value = data.get(key);

            return new KeyAttribute(key, value instanceof Date ? new DateTime(value).toString() : value);
        };

        val eventItem = new Item().
                withPrimaryKey(new PrimaryKey(
                                keyAttribute.apply("sessionId"),
                                keyAttribute.apply("dateTime")
                        )
                ).
                withKeyComponents(keyAttribute.apply("pageNumber")).
                withKeyComponents(keyAttribute.apply("feedback"));

        val pageItem = new UpdateItemSpec().
                withPrimaryKey(keyAttribute.apply("pageNumber")).
                withUpdateExpression("ADD #attr :value").
                withNameMap(ImmutableMap.of("#attr", "totalVisits")).
                withValueMap(new ValueMap().withInt(":value", 1));


        CompletableFuture.supplyAsync(() -> {

            getEventsTable().putItem(eventItem);
            return getPagesTable().updateItem(pageItem);

        }).exceptionally(ex -> {

            Logger.error("DynamoDB error", ex);
            return null;
        });
    }

    private static List<Map<String, Object>> queryResult(ItemCollection<QueryOutcome> query) {

        return StreamSupport.stream(query.pages().spliterator(), false).
                map(Page::getLowLevelResult).
                map(QueryOutcome::getItems).
                flatMap(List::stream).
                map(Item::asMap).
                collect(Collectors.toList());
    }

    public CompletableFuture<List<Map<String, Object>>> sessionEvents(String sessionId) {

        val query = getEventsTable().query(new QuerySpec().withHashKey("sessionId", sessionId));

        return CompletableFuture.supplyAsync(() -> queryResult(query));
    }

    @Override
    public CompletableFuture<List<Map<String, Object>>> recentEvents(int limit) {

        val index = getEventsTable().getIndex("pages");

        return pageVisits().thenApplyAsync(pages -> {

            val events = pages.keySet().stream().
                    map(page -> index.query(
                            new QuerySpec().withHashKey("pageNumber", page).
                                    withScanIndexForward(false).
                                    withMaxResultSize(limit))
                    ).
                    map(DynamoDbStore::queryResult).
                    flatMap(List::stream).
                    sorted(Comparator.comparing(item -> item.get("dateTime").toString())).
                    collect(Collectors.toList());

            Collections.reverse(events);

            return events.stream().limit(limit).collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<Map<Integer, Integer>> pageVisits() {

        return CompletableFuture.supplyAsync(() ->

                StreamSupport.stream(new IterableScan(amazon, new ScanRequest("pages")).spliterator(),false).
                        map(ScanResult::getItems).
                        flatMap(List::stream).
                        collect(Collectors.toMap(
                                item -> numberAttribute(item, "pageNumber"),
                                item -> numberAttribute(item, "totalVisits")
                        ))
        );
    }

    private Table getTable(String name, Function<CreateTableRequest, CreateTableRequest> request) {

        TableDescription description;
        Table table = dynamoClient.getTable(name);

        try {

            description = table.describe();
        }
        catch (ResourceNotFoundException ex) {

            description = null;
        }

        if (description == null) {

            Logger.info("Creating DynamoDB table: " + table.getTableName());

            table = dynamoClient.createTable(request.apply(new CreateTableRequest().withTableName(table.getTableName())));

            try {

                table.waitForActive();
            }
            catch (InterruptedException ex) {

                Logger.error("DynamoDB error", ex);
                table = null;
            }
        }

        return table;
    }

    private Table getEventsTable() {

        return getTable("events", request ->

                request.withKeySchema(ImmutableList.of(
                        new KeySchemaElement("sessionId", "HASH"),
                        new KeySchemaElement("dateTime", "RANGE")
                )).withAttributeDefinitions(
                        new AttributeDefinition("sessionId", ScalarAttributeType.S),
                        new AttributeDefinition("dateTime", ScalarAttributeType.S),
                        new AttributeDefinition("pageNumber", ScalarAttributeType.N)
                ).withProvisionedThroughput(
                        new ProvisionedThroughput(5L, 5L)
                ).withGlobalSecondaryIndexes(
                        new GlobalSecondaryIndex().withIndexName("pages").withKeySchema(
                                new KeySchemaElement("pageNumber", "HASH"),
                                new KeySchemaElement("dateTime", "RANGE")
                        ).withProjection(
                                new Projection().withProjectionType(ProjectionType.ALL)
                        ) .withProvisionedThroughput(
                                new ProvisionedThroughput(5L, 5L)
                        )
                )
        );
    }

    private Table getPagesTable() {

        return getTable("pages", request ->

                request.withKeySchema(
                        new KeySchemaElement("pageNumber", "HASH")
                ).withAttributeDefinitions(
                        new AttributeDefinition("pageNumber", ScalarAttributeType.N)
                ).withProvisionedThroughput(
                        new ProvisionedThroughput(5L, 5L)
                )
        );
    }

    private static Integer numberAttribute(Map<String, AttributeValue> item, String name) {

        return Integer.valueOf(item.get(name).getN());
    }
}
