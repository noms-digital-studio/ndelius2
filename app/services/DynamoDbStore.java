package services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import helpers.IterableScan;
import interfaces.AnalyticsStore;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
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

    private Table getPageTable(String pageNumber) {

        TableDescription description;
        Table table = dynamoClient.getTable("page" + pageNumber);

        try {

            description = table.describe();
        }
        catch (ResourceNotFoundException ex) {

            description = null;
        }

        if (description == null) {

            Logger.info("Creating DynamoDB table: " + table.getTableName());

            val request = new CreateTableRequest(table.getTableName(), ImmutableList.of(
                    new KeySchemaElement("dateTime", "HASH"),
                    new KeySchemaElement("sessionId", "RANGE")
            )).withAttributeDefinitions(
                    new AttributeDefinition("dateTime", ScalarAttributeType.S),
                    new AttributeDefinition("sessionId", ScalarAttributeType.S)
            ).withProvisionedThroughput(new ProvisionedThroughput(5L, 5L));

            table = dynamoClient.createTable(request);

            try {

                table.waitForActive();
            }
            catch (InterruptedException ex) {

                table = null;
            }
        }

        return table;
    }

    @Override
    public void recordEvent(Map<String, Object> data) {

        final BiFunction<String, Function<Object, Object>, KeyAttribute> keyAttribute = (key, transform) ->
                new KeyAttribute(key, transform.apply(data.get(key)));

        val eventItem = new Item().
                withPrimaryKey(new PrimaryKey(
                                keyAttribute.apply("dateTime", dateTime -> new DateTime(dateTime).toString()),
                                keyAttribute.apply("sessionId", Function.identity())
                        )
                ).
                with("feedback", data.get("feedback"));

        CompletableFuture.supplyAsync(() -> getPageTable(data.get("pageNumber").toString()).putItem(eventItem));
    }

    @Override
    public CompletableFuture<List<Map<String, Object>>> recentEvents(int limit) {

        return CompletableFuture.supplyAsync(() -> ImmutableList.of(ImmutableMap.of())); //@TODO: Not easy in DynamoDB
    }

    @Override
    public CompletableFuture<Map<Integer, Integer>> pageVisits() {

        return CompletableFuture.supplyAsync(() ->

                StreamSupport.stream(dynamoClient.listTables().pages().spliterator(), false).
                        map(Page::getLowLevelResult).
                        map(ListTablesResult::getTableNames).
                        flatMap(List::stream).
                        filter(table -> table.startsWith("page")).
                        collect(Collectors.toMap(table -> Integer.valueOf(table.replace("page", "")), table ->

                                StreamSupport.stream(
                                        new IterableScan(amazon, new ScanRequest(table)).spliterator(),
                                        false
                                ).mapToInt(ScanResult::getCount).sum()
                        ))
        );
    }
}
