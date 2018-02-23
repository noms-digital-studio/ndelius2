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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
import static java.util.Arrays.asList;

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
                            ImmutableMap.of("$sum", 1L)
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
    public CompletableFuture<Map<Integer, Long>> rankGrouping(String eventType, LocalDateTime from) {
        val result = new CompletableFuture<Map<Integer, Long>>();

        val hasRank = _match( _exists("rankIndex"));
        val match = _match( _eq("type", eventType));
        val dateFilter = _match( _gte("dateTime", from));
        val sum = _group(_by("_id", "$rankIndex", "total", _sum()));
        val sort = _sort("_id", 1);
        val group = ImmutableList.of(hasRank, dateFilter, match, sum, sort);

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
    public CompletableFuture<Map<String, Long>> eventOutcome(String eventType, LocalDateTime from) {
        val result = new CompletableFuture<Map<String, Long>>();

        val hasCorrelationId = _match(_exists("correlationId"));
        val dateFilter = _match( _gte("dateTime", from));
        val lastType = _group(_by("_id", "$correlationId", "lastType", _last("$type")));
        val sum = _group(_by("_id", "$lastType", "total", _sum()));
        val sort = _sort("_id", 1);

        val eventOutcome = ImmutableList.of(hasCorrelationId, dateFilter, lastType, sum, sort);

        events.aggregate(eventOutcome).
                toObservable().
                toList().
                map(documents -> documents.stream().collect(
                        Collectors.toMap(doc -> doc.getString("_id"), doc -> doc.getLong("total")))
                ).
                doOnError(result::completeExceptionally).
                subscribe(result::complete);

        return result;
    }

    @Override
    public CompletableFuture<Map<Long, Long>> durationBetween(String firstEventType, String secondEventType, LocalDateTime from, long groupBySeconds) {
        val result = new CompletableFuture<Map<Long, Long>>();
        val hasCorrelationId = _match(_exists("correlationId"));
        val dateFilter = _match(_gte("dateTime", from));
        val eventTypeFilter = _match(_or(_eq("type", firstEventType), _eq("type", secondEventType)));
        val firstAndLastDates = _group(_by(
                        "_id", "$correlationId",
                        "firstDateTime", _first("$dateTime"),
                        "firstType", _first("$type"),
                        "lastDateTime", _last("$dateTime"),
                        "lastType", _last("$type")
                ));
        val firstAndLastMatchingEventType = _match(_and(_eq("firstType", firstEventType), _eq("lastType", secondEventType)));
        val durationInMills = _project(ImmutableMap.of(
                "durationInMills",
                _subtract("$lastDateTime", "$firstDateTime")
                )
        );
        val duration = _project(ImmutableMap.of(
                "duration",
                _divide("$durationInMills", 1000 * groupBySeconds))
        );
        val roundedDuration = _project(ImmutableMap.of(
                "roundedDuration",
                _ceil("$duration"))
        );
        val sum = _group(_by("_id", "$roundedDuration", "total", _sum()));
        val sort = _sort("_id", 1);

        val durationBetween = ImmutableList.of(
                hasCorrelationId,
                dateFilter,
                eventTypeFilter,
                firstAndLastDates,
                firstAndLastMatchingEventType,
                durationInMills,
                duration,
                roundedDuration,
                sum,
                sort);

        events.aggregate(durationBetween).
                toObservable().
                toList().
                map(documents -> documents.stream().collect(
                        Collectors.toMap(doc -> doc.getDouble("_id").longValue(), doc -> doc.getLong("total"), firstDuplicate(), LinkedHashMap::new))
                ).
                doOnError(result::completeExceptionally).
                subscribe(result::complete);

        return result;
    }

    @Override
    public CompletableFuture<Map<Integer, Long>> countGrouping(String eventType, String countFieldName, LocalDateTime from, long groupByScale) {
        val result = new CompletableFuture<Map<Integer, Long>>();
        val match = _match( _eq("type", eventType));
        val hasRank = _match( _exists(countFieldName));
        val dateFilter = _match( _gte("dateTime", from));
        val scaledDownCount = _project(ImmutableMap.of(
                "scaledDownCount",
                _divide("$" + countFieldName, groupByScale))
        );
        val roundedCount = _project(ImmutableMap.of(
                "roundedCount",
                _ceil("$scaledDownCount"))
        );
        val groupedScaleCount = _project(ImmutableMap.of(
                "groupedScaleCount",
                _multiply("$roundedCount", groupByScale))
        );
        val sum = _group(_by("_id", "$groupedScaleCount", "total", _sum()));
        val sort = _sort("_id", 1);

        val countGrouping = ImmutableList.of(
                match,
                hasRank,
                dateFilter,
                scaledDownCount,
                roundedCount,
                groupedScaleCount,
                sum,
                sort);

        events.aggregate(countGrouping).
                toObservable().
                toList().
                map(documents -> documents.stream().collect(
                        Collectors.toMap(doc -> doc.getDouble("_id").intValue(), doc -> doc.getLong("total"), firstDuplicate(), LinkedHashMap::new))
                ).
                doOnError(result::completeExceptionally).
                subscribe(result::complete);

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
        return gte("dateTime", toDate(from));
    }

    private Date toDate(LocalDateTime from) {
        return Date.from(from.atZone(ZoneId.systemDefault()).toInstant());
    }

    private Document _last(String field) {
        return new Document(
                ImmutableMap.of("$last", field)
        );
    }

    private Document _first(String field) {
        return new Document(
                ImmutableMap.of("$first", field)
        );
    }
    private Document _ceil(String field) {
        return new Document(
                ImmutableMap.of("$ceil", field)
        );
    }

    private Document _match(Document document) {
        return new Document(
                ImmutableMap.of(
                        "$match", document
                ));
    }

    private Document _exists(String field) {
        return new Document(
                ImmutableMap.of(
                        field, new Document(
                                "$exists", true
                        )
                ));
    }
    private Document _eq(String field, String value) {
        return new Document(
                ImmutableMap.of(
                        field, new Document(
                                "$eq", value
                        )
                ));
    }

    private Document _gte(String field, LocalDateTime date) {
        return new Document(
                ImmutableMap.of(
                        field, new Document(
                                "$gte", toDate(date)
                        )
                ));
    }

    private Document _sort(String field, int direction) {
        return new Document(ImmutableMap.of(
                "$sort", new Document(ImmutableMap.of(
                        field, direction
                ))
        ));
    }
    private Document _group(Document document) {
        return new Document(
                ImmutableMap.of(
                        "$group", document
                ));

    }
    private Document _project(Map projection) {
        return new Document(
                ImmutableMap.of(
                        "$project", projection
                ));

    }
    private Document _or(Document document1, Document document2) {
        return new Document(
                ImmutableMap.of(
                        "$or", asList(document1, document2)
                ));

    }
    private Document _and(Document document1, Document document2) {
        return new Document(
                ImmutableMap.of(
                        "$and", asList(document1, document2)
                ));

    }
    private Document _sum() {
        return new Document(
                ImmutableMap.of("$sum", 1L)
        );
    }

    private Document _by(String resultFieldName, String groupFieldName, String aggregateFieldName, Document aggregation) {
        return new Document(
                ImmutableMap.of(
                        resultFieldName, groupFieldName,
                        aggregateFieldName, aggregation
                ));
    }

    private Document _by(String resultFieldName, String groupFieldName, String aggregateFieldName1, Document aggregation1, String aggregateFieldName2, Document aggregation2, String aggregateFieldName3, Document aggregation3, String aggregateFieldName4, Document aggregation4) {
        return new Document(
                ImmutableMap.of(
                        resultFieldName, groupFieldName,
                        aggregateFieldName1, aggregation1,
                        aggregateFieldName2, aggregation2,
                        aggregateFieldName3, aggregation3,
                        aggregateFieldName4, aggregation4
                ));
    }

    private Document _subtract(Object first, Object second) {
        return new Document(ImmutableMap.of("$subtract", asList(first, second)));
    }

    private Document _divide(Object first, Object second) {
        return new Document(ImmutableMap.of("$divide", asList(first, second)));
    }

    private Document _multiply(Object first, Object second) {
        return new Document(ImmutableMap.of("$multiply", asList(first, second)));
    }

    private BinaryOperator<Long> firstDuplicate() {
        return (key1, key2) -> key1;
    }


}
