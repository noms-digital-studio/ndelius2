package services;

import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import helpers.JsonHelper;
import interfaces.DocumentStore;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import play.mvc.Http.MultipartFormData.DataPart;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Http.MultipartFormData.Part;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static play.mvc.Http.Status.OK;

public class AlfrescoStore implements DocumentStore {

    private final String alfrescoUrl;
    private final String alfrescoUser;
    private final String offenderApiUrl;
    private final WSClient wsClient;

    @Inject
    public AlfrescoStore(Config configuration, WSClient wsClient) {

        alfrescoUrl = configuration.getString("store.alfresco.url");
        alfrescoUser = configuration.getString("store.alfresco.user");
        offenderApiUrl = configuration.getString("offender.api.url");

        this.wsClient = wsClient;
    }

    @Override
    public CompletionStage<Map<String, String>> uploadNewPdf(Byte[] document, String filename, String onBehalfOfUser, String originalData, String crn, Long entityId) {

        val parameters = new HashMap<String, String>() {
            {
                put("CRN", crn);
                put("author", onBehalfOfUser);
                put("entityType", "COURTREPORT");
                put("entityId", Optional.ofNullable(entityId).map(Object::toString).orElse(""));
                put("docType", "DOCUMENT");
                put("userData", originalData);
            }
        };

        return postFileUpload(filename, document, onBehalfOfUser, "uploadnew", parameters).thenCompose(stored -> {

            val documentId = stored.get("ID");

            if (Strings.isNullOrEmpty(documentId)) {

                val errorMessage = "No Alfresco Document ID retrieved for document: " + filename ;

                Logger.error(errorMessage);
                stored.put("errorMessage", errorMessage);

                return CompletableFuture.completedFuture(stored);

            } else {

                val documentLink = new HashMap<String, String>() {
                    {
                        put("alfrescoId", documentId);
                        put("alfrescoUser", onBehalfOfUser);
                        put("probationAreaCode", alfrescoUser);
                        put("documentName", filename);
                        put("crn", crn);
                        put("tableName", "COURT_REPORT");
                        put("entityId", Optional.ofNullable(entityId).map(Object::toString).orElse(""));
                    }
                };

                Logger.info("Creating Delius Document Link: " + documentLink.toString());

                return wsClient.url(offenderApiUrl + "documentLink").
                        post(Json.toJson(documentLink)).
                        thenApply(WSResponse::getStatus).
                        thenApply(status -> {

                            if (status != Http.Status.CREATED) {

                                val errorMessage = String.format("Delius Document Link Result: %d", status);

                                Logger.error(errorMessage);
                                stored.put("errorMessage", errorMessage);
                            }

                            return stored;
                        });
            }
        });
    }

    @Override
    public CompletionStage<String> retrieveOriginalData(String documentId, String onBehalfOfUser) {
        return getDocumentMetaData(documentId, onBehalfOfUser).
                thenApply(result -> result.get("userData"));
    }

    @Override
    public CompletionStage<byte[]> retrieveDocument(String documentId, String onBehalfOfUser) {
        return makeRequest("fetch/" + documentId, onBehalfOfUser).get().
                thenApply(WSResponse::asByteArray);
    }

    @Override
    public CompletionStage<String> getDocumentName(String documentId, String onBehalfOfUser) {
        return getDocumentMetaData(documentId, onBehalfOfUser).
                thenApply(result -> result.get("name"));
    }

    @Override
    public CompletionStage<Integer> lockDocument(String onBehalfOfUser, String documentId) {

        return makeRequest("reserve/" + documentId, onBehalfOfUser).
                post(Source.from(ImmutableList.of())).
                thenApply(WSResponse::getStatus);
    }

    @Override
    public CompletionStage<Map<String, String>> updateExistingPdf(Byte[] document, String filename, String onBehalfOfUser, String updatedData, String documentId) {

        val multiResult = new HashMap<String, String>();

        return postFileUpload(
                filename,
                document,
                onBehalfOfUser,
                "uploadandrelease/" + documentId,
                ImmutableMap.of("author", onBehalfOfUser)).
                exceptionally(error -> {

                    Logger.error("Upload and Release error", error);
                    return ImmutableMap.of();
                }).
                thenCompose(singleResult -> {

                    multiResult.putAll(singleResult);

                    return makeRequest("updatemetadata/" + documentId, onBehalfOfUser).
                            post(Source.from(new ArrayList<>(mapToParts(ImmutableMap.of("userData", updatedData))))).
                            thenApply(WSResponse::asJson).
                            thenApply(JsonHelper::jsonToMap);
                }).
                exceptionally(error -> {

                    Logger.error("Update Meta Data error", error);
                    return ImmutableMap.of();
                }).
                thenApply(singleResult -> {

                    multiResult.putAll(singleResult);

                    return multiResult;
                });
    }

    @Override
    public CompletionStage<Boolean> isHealthy() {
        return wsClient.url(alfrescoUrl + "noms-spg/notificationStatus")
            .addHeader("X-DocRepository-Remote-User", alfrescoUser)
            .get()
            .thenApply(wsResponse -> {
                if (wsResponse.getStatus() != OK) {
                    Logger.warn("Error calling Alfresco. Status {}", wsResponse.getStatus());
                    return false;
                }
                return true;
            })
            .exceptionally(throwable -> {
                Logger.warn("Error calling Alfresco", throwable);
                return false;
            });
    }

    private WSRequest makeRequest(String operation, String onBehalfOfUser) {

        return wsClient.url(alfrescoUrl + "noms-spg/" + operation).
                addHeader("X-DocRepository-Remote-User", alfrescoUser).
                addHeader("X-DocRepository-Real-Remote-User", onBehalfOfUser);
    }

    private CompletionStage<Map<String, String>> getDocumentMetaData(String documentId, String onBehalfOfUser) {
        return makeRequest("details/" + documentId, onBehalfOfUser).get().
                thenApply(WSResponse::asJson).
                thenApply(JsonHelper::jsonToMap);
    }

    private CompletionStage<Map<String, String>> postMultiPartForm(String onBehalfOfUser, String operation, List<Part> parts) {

        return makeRequest(operation, onBehalfOfUser).
                post(Source.from(parts)).
                thenApply(WSResponse::asJson).
                thenApply(JsonHelper::jsonToMap);
    }

    private CompletionStage<Map<String, String>> postFileUpload(String filename, Byte[] document, String onBehalfOfUser, String operation, Map<String, String> parameters) {

        try {

            val uploadFile = createTempFileName("upload", ".pdf");

            Files.write(uploadFile.toPath(), ArrayUtils.toPrimitive(document));

            val parts = new ArrayList<Part>();
            parts.add(new FilePart("filedata", filename, "application/pdf", FileIO.fromPath(uploadFile.toPath())));
            parts.addAll(mapToParts(parameters));

            Logger.info("Storing PDF: " + filename);

            return postMultiPartForm(onBehalfOfUser, operation, parts);
        }
        catch (IOException ex) {

            Logger.error("Upload error", ex);

            return CompletableFuture.supplyAsync(ImmutableMap::of);
        }
    }

    private static List<DataPart> mapToParts(Map<String, String> map) {

        return map.entrySet().stream().map(entry -> new DataPart(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }

    private static File createTempFileName(String prefix, String suffix) throws IOException {

        val tempTile = File.createTempFile(prefix, suffix);

        tempTile.delete();
        return tempTile;
    }
}
