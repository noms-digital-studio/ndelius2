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
import services.helpers.MongoUtils;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

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
                map(document -> document.entrySet().stream().collect(toMap(Map.Entry::getKey, entry -> {

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
                        toMap(doc -> doc.getInteger("_id"), doc -> doc.getLong("total")))
                ).
                doOnError(result::completeExceptionally).
                subscribe(result::complete);

        return result;
    }

    @Override
    public CompletableFuture<Map<String, Object>> weeklySatisfactionScores() {

        val result = new CompletableFuture<Map<String, Object>>();

        /*
        $match:
	     {
		 type: { $eq: "search-feedback"}
		 }
         */
        val match = _match( _eq("type", "search-feedback"));

        /*
            "$group": {
              "_id": {
                "week": {
                  "$week": "$dateTime"
                },
                "year": {
                  "$year": "$dateTime"
                },
                "rating": "$feedback.rating"
              },
              "count": {
                "$sum": 1
              }
            }
	     */
        val group1 = new Document(ImmutableMap.of(
            "$group", new Document(ImmutableMap.of(
                "_id", new Document(ImmutableMap.of(
                    "week", new Document(ImmutableMap.of("$week", "$dateTime")),
                    "year", new Document(ImmutableMap.of("$year", "$dateTime")),
                    "rating", "$feedback.rating")),
                "count", _sum()
            ))
        ));

        /*
            "$project": {
              "rating": "$_id.rating",
              "weeklyCounts": {  "yearAndWeek": { "$concat": [
                { "$substr": [ "$_id.year", 0, 4 ] },
                "-",
                { "$substr": [ "$_id.week", 0, 2 ] }
              ]}, "count": "$count" },

              "_id": 0
            }
        */
        val project = new Document(ImmutableMap.of(
            "$project", new Document(ImmutableMap.of(
                "rating", "$_id.rating",
                "weeklyCounts", new Document(ImmutableMap.of(
                    "yearAndWeek", new Document(ImmutableMap.of(
                        "$concat", asList(
                            new Document(ImmutableMap.of("$substr", asList("$_id.year", 0, 4))),
                            "-",
                            new Document(ImmutableMap.of("$substr", asList("$_id.week", 0, 2)))
                        )
                    )),
                    "count", "$count"
                )),
                "_id", 0
            ))
        ));

        /*
          "$group": {
            "_id": "$rating",
            "weeklyCounts": {
                "$push": "$weeklyCounts"
                }
           }
         */
        val group2 = new Document(ImmutableMap.of(
            "$group", new Document(ImmutableMap.of(
                "_id", "$rating",
                "weeklyCounts", new Document(ImmutableMap.of(
                    "$push", "$weeklyCounts"
                ))
            ))
        ));

        val pipeline = ImmutableList.of(match, group1, project, group2);

        events.aggregate(pipeline).
            toObservable().
            toList().
            map(documents -> documents.stream()
                .collect(
                    toMap(document -> document.getString("_id"),
                          document -> document.get("weeklyCounts")
                    )
                )
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
                        toMap(doc -> doc.getInteger("_id"), doc -> doc.getLong("total")))
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
                        toMap(doc -> doc.getString("_id"), doc -> doc.getLong("total")))
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
                sort,
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
                        toMap(doc -> doc.getDouble("_id").longValue(), doc -> doc.getLong("total"), firstDuplicate(), LinkedHashMap::new))
                ).
                doOnError(result::completeExceptionally).
                subscribe(result::complete);

        return result;
    }

    @Override
    public CompletableFuture<Map<String, Long>> countGroupingArray(String eventType, String countFieldName, LocalDateTime from) {
        val result = new CompletableFuture<Map<String, Long>>();
        val match = _match( _eq("type", eventType));
        val hasCountField = _match( _exists(countFieldName));
        val dateFilter = _match( _gte("dateTime", from));
        val deconstructedArray = _unwind( countFieldName);
        val sum = _group(_by("_id", "$"+countFieldName, "total", _sum()));
        val sort = _sort("total", 1);

        val countGrouping = ImmutableList.of(
                match,
                hasCountField,
                dateFilter,
                deconstructedArray,
                sum,
                sort);

        events.aggregate(countGrouping).
                toObservable().
                toList().
                map(documents -> documents.stream().collect(
                        toMap(doc -> doc.getString("_id"), doc -> doc.getLong("total")))
                ).
                doOnError(result::completeExceptionally).
                subscribe(result::complete);


        return result;
    }

    @Override
    public CompletableFuture<Boolean> isUp() {
        return MongoUtils.isHealthy(database);
    }

    @Override
    public CompletableFuture<List<Map<String, Object>>> nationalSearchFeedback() {
        val result = new CompletableFuture<List<Map<String, Object>>>();

        events.find().
                projection(new Document(ImmutableMap.of(
                        "_id", 0,
                        "dateTime", 1,
                        "username", 1,
                        "feedback", 1
                ))).
                filter(eq("type", "search-feedback")).
                sort(new Document("$natural", -1)).
                limit(1000).
                toObservable().
                map(document -> document.entrySet().stream().collect(toMap(Map.Entry::getKey, Map.Entry::getValue))).
                toList().
                doOnError(result::completeExceptionally).
                subscribe(result::complete);

        return result;
    }

    @Override
    public CompletableFuture<List<Map<String, Object>>> sfpsrFeedback() {
        val result = new CompletableFuture<List<Map<String, Object>>>();

        events.find().
                projection(new Document(ImmutableMap.of(
                        "_id", 0,
                        "dateTime", 1,
                        "username", 1,
                        "feedback", 1
                ))).
                filter( and(
                        _exists("type", false),
                        _notNull("feedback"),
                        _notEmpty("feedback"),
                        _or(
                            _type("feedback", "string"),
                            _and(_type("feedback", "object"), _notNull("feedback.feedback")))) ).
                sort(new Document("$natural", -1)).
                limit(1000).
                toObservable().
                map(document -> document.entrySet().stream().collect(toMap(Map.Entry::getKey, Map.Entry::getValue))).
                toList().
                doOnError(result::completeExceptionally).
                subscribe(result::complete);

        return result;
    }

    @Override
    public CompletableFuture<Map<String, Integer>> filterCounts(LocalDateTime from) {

        CompletableFuture<Map<String, Integer>> result = new CompletableFuture<>();
        val hasCorrelationId = _match(_exists("correlationId"));
        val dateFilter = _match(_gte("dateTime", from));
        val match = _match( _eq("type", "search-request"));
        val hasFilterAnalytics = _match(_exists("filter"));

        val lastFilter = _group(_by(
                "_id", "$correlationId",
                "lastFilter", _last("$filter")
        ));
        val usedFilters = _project(ImmutableMap.of(
                "hasUsedMyProvidersFilter",
                _cond(
                        _and(_gt("$lastFilter.myProviderSelectedCount", 0), _eq("$lastFilter.otherProviderSelectedCount", 0)),
                        1,
                        0),
                "hasUsedOtherProvidersFilter",
                _cond(
                        _and(_gt("$lastFilter.otherProviderSelectedCount", 0), _eq("$lastFilter.myProviderSelectedCount", 0)),
                        1,
                        0),
                "hasUsedBothProvidersFilter",
                _cond(
                        _and(_gt("$lastFilter.otherProviderSelectedCount", 0), _gt("$lastFilter.myProviderSelectedCount", 0)),
                        1,
                        0),
                "hasNotUsedFilter",
                _cond(
                        _and(_eq("$lastFilter.otherProviderSelectedCount", 0), _eq("$lastFilter.myProviderSelectedCount", 0)),
                        1,
                        0)
                )

        );

        val sum = _group(new Document(
                ImmutableMap.of(
                        "_id", "singleton",
                        "hasUsedMyProvidersFilterCount", _sum("$hasUsedMyProvidersFilter"),
                        "hasUsedOtherProvidersFilterCount", _sum("$hasUsedOtherProvidersFilter"),
                        "hasUsedBothProvidersFilterCount", _sum("$hasUsedBothProvidersFilter"),
                        "hasNotUsedFilterCount", _sum("$hasNotUsedFilter")
                )));

        val sort = _sort("_id", 1);

        val aggregation = ImmutableList.of(
                hasCorrelationId,
                dateFilter,
                match,
                hasFilterAnalytics,
                sort,
                lastFilter,
                usedFilters,
                sum
                );

        events.aggregate(aggregation).
                toObservable().
                map(document ->
                        document.entrySet().stream()
                                .filter(entry -> !entry.getKey().equals("_id"))
                                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Integer.valueOf(entry.getValue().toString())))).
                defaultIfEmpty(ImmutableMap.of()).
                doOnError(result::completeExceptionally).
                subscribe(result::complete);

        return result;
    }

    @Override
    public CompletableFuture<Map<String, Long>> userAgentTypeCounts(String eventType, LocalDateTime from) {
        val result = new CompletableFuture<Map<String, Long>>();
        val dateFilter = _match( _gte("dateTime", from));
        val match = _match( _eq("type", eventType));
        val matchClient = _match( _exists("client"));

        val project = _project(new Document(ImmutableMap.of(
                "_id", 0,
                "browser", _concat("$client.user_agent.family", " ", "$client.user_agent.major")
        )));

        val sum = _group(_by("_id", "$browser", "total", _sum()));
        val sort = _sort("_id", 1);

        val countGrouping = ImmutableList.of(
                dateFilter,
                match,
                matchClient,
                project,
                sum,
                sort);

        events.aggregate(countGrouping).
                toObservable().
                toList().
                map(documents -> documents.stream()
                        .collect(
                            toMap(doc -> doc.getString("_id"), doc -> doc.getLong("total"), firstDuplicate(), LinkedHashMap::new))
                ).
                doOnError(result::completeExceptionally).
                subscribe(result::complete);


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

    private Document _type(String field, String type) {
        return new Document(
                ImmutableMap.of(
                        field, new Document(
                                "$type", type
                        )
                ));
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
        return _exists(field, true);
    }
    private Document _exists(String field, boolean exists) {
        return new Document(
                ImmutableMap.of(
                        field, new Document(
                                "$exists", exists
                        )
                ));
    }
    private Document _notNull(String field) {
        return new Document(
                ImmutableMap.of(
                        field, new Document(
                                "$ne", null
                        )
                ));
    }
    private Document _notEmpty(String field) {
        return new Document(
                ImmutableMap.of(
                        field, new Document(
                                "$ne", ""
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

    private Document _eq(String field, int value) {
        return new Document(
                ImmutableMap.of(
                        "$eq", asList(field, value)
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
    private Document _gt(String field, long number) {
        return new Document(
                ImmutableMap.of(
                        "$gt", asList(field, number)
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

    private Document _cond(Document condition, int whenTrue, int whenFalse) {
        return new Document(
                ImmutableMap.of("$cond", asList(condition, whenTrue, whenFalse))
        );
    }

    private Document _sum(String field) {
        return new Document(
                ImmutableMap.of("$sum", field)
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

    private Document _unwind(String field) {
        return new Document(ImmutableMap.of("$unwind", "$" + field));
    }

    private BinaryOperator<Long> firstDuplicate() {
        return (key1, key2) -> key1;
    }

    private Document _concat(String ... elements) {
        return new Document(ImmutableMap.of("$concat", asList(elements)));
    }


}
