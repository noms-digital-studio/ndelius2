package services.fakes;

import com.google.common.collect.ImmutableMap;
import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoCollection;
import helpers.Encryption;
import interfaces.DocumentStore;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.util.encoders.Base64;
import org.bson.Document;
import org.bson.types.ObjectId;
import play.Logger;

import javax.inject.Inject;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static com.mongodb.client.model.Filters.eq;

public class MongoDocumentStore implements DocumentStore {

    private final MongoCollection<Document> shortFormatReports;

    @Inject
    public MongoDocumentStore(MongoClient mongoClient) {
        shortFormatReports = mongoClient.getDatabase("documents").getCollection("shortFormatReports");
    }

    @Override
    public CompletionStage<Map<String, String>> uploadNewPdf(Byte[] document, String filename, String onBehalfOfUser,
                                                             String originalData, String crn, Long entityId) {
        val documentBytes = ArrayUtils.toPrimitive(document);
        val doc = Base64.toBase64String(documentBytes);
        val key = ObjectId.get();
        val parameters = new HashMap<String, Object>() {
            {
                put("document", doc);
                put("filename", filename);
                put("onBehalfOfUser", onBehalfOfUser);
                put("originalData", originalData);
                put("entityId", entityId != null ? entityId.toString() : "");
                put("crn", crn);
                put("_id", key);
            }
        };

        shortFormatReports.insertOne(new Document(parameters)).subscribe(
            success -> { },
            error -> Logger.error("uploadNewPdf insert error", error)
        );

        Logger.debug(String.format("uploadNewPdf: storing pdf against key %s", key.toString()));
        Logger.debug(String.format("URL encoded encrypted key %s", URLEncoder.encode(Encryption.encrypt(key.toString(), "ThisIsASecretKey"))));
        return CompletableFuture.completedFuture(ImmutableMap.of("ID", key.toString()));
    }

    @Override
    public CompletionStage<String> retrieveOriginalData(String documentId, String onBehalfOfUser) {

        val result = new CompletableFuture<String>();
        shortFormatReports
            .find(eq("_id", new ObjectId(documentId)))
            .first()
            .doOnError(result::completeExceptionally)
            .subscribe(thing -> result.complete((String)thing.get("originalData")));

        Logger.debug(String.format("retrieveOriginalData: for key %s", documentId));
        return result;
    }

    @Override
    public CompletionStage<Integer> lockDocument(String onBehalfOfUser, String documentId) {
        return CompletableFuture.completedFuture(500);
    }

    @Override
    public CompletionStage<Map<String, String>> updateExistingPdf(Byte[] document, String filename, String onBehalfOfUser, String updatedData, String documentId) {
        val findResult = new CompletableFuture<Map<String, Object>>();
        shortFormatReports
            .find(eq("_id", new ObjectId(documentId)))
            .first()
            .doOnError(findResult::completeExceptionally)
            .subscribe(findResult::complete);

        return findResult.thenApply(result -> {
            val entityId = (String) result.get("entityId");

            val documentBytes = ArrayUtils.toPrimitive(document);
            val doc = Base64.toBase64String(documentBytes);
            val newParameters = new HashMap<String, Object>() {
                {
                    put("document", doc);
                    put("filename", filename);
                    put("onBehalfOfUser", onBehalfOfUser);
                    put("originalData", updatedData);
                    put("entityId", entityId != null ? entityId : "");
                }
            };

            shortFormatReports
                .updateOne(eq("_id", new ObjectId(documentId)), new Document("$set",new Document(newParameters)))
                .subscribe(
                    success -> { },
                    error -> Logger.error("updateExistingPdf insert error", error)
                );

            Logger.debug(String.format("updateExistingPdf: storing pdf against key %s", documentId));
            return ImmutableMap.of("ID", documentId);
        });
    }

    @Override
    public CompletionStage<Boolean> isHealthy() {
        return CompletableFuture.completedFuture(true);
    }
}
