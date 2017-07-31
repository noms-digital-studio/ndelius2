package services;

import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import helpers.JsonHelper;
import interfaces.DocumentStore;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Http.MultipartFormData.*;

public class AlfrescoStore implements DocumentStore {

    private final String alfrescoUrl;
    private final String alfrescoUser;
    private final WSClient wsClient;

    @Inject
    public AlfrescoStore(Config configuration, WSClient wsClient) {

        alfrescoUrl = configuration.getString("store.alfresco.url");
        alfrescoUser = configuration.getString("store.alfresco.user");

        this.wsClient = wsClient;
    }

    @Override
    public CompletionStage<Map<String, String>> uploadNewPdf(Byte[] document, String filename, String onBehalfOfUser, String originalData, String crn, Long entityId) {

        val parameters = new HashMap<String, String>() {
            {
                put("CRN", crn);
                put("author", onBehalfOfUser);
                put("entityType", "COURTREPORT");
                put("entityId", entityId.toString());
                put("docType", "DOCUMENT");
                put("userData", originalData);
                put("forceBroadcast", "true");
            }
        };

        return postFileUpload(filename, document, onBehalfOfUser, "uploadnew", parameters);
    }

    @Override
    public CompletionStage<String> retrieveOriginalData(String documentId, String onBehalfOfUser) {

        return makeRequest("details/" + documentId, onBehalfOfUser).get().
                thenApply(WSResponse::asJson).
                thenApply(JsonHelper::jsonToMap).
                thenApply(result -> result.get("userData"));
    }

    @Override
    public CompletionStage<Integer> lockDocument(String onBehalfOfUser, String documentId) {

        return makeRequest("reserve/" + documentId, onBehalfOfUser).
                post(Source.from(ImmutableList.of())).
                thenApply(WSResponse::getStatus);
    }

    @Override
    public CompletionStage<Map<String, String>> updateExistingPdf(Byte[] document, String filename, String onBehalfOfUser, String updatedData, String documentId) {

        val updateDocument = postFileUpload(filename, document, onBehalfOfUser, "uploadandrelease/" + documentId, ImmutableMap.of(
                "author", onBehalfOfUser
        ));

        val updateMetaData = makeRequest("updatemetadata/" + documentId, onBehalfOfUser).
                post(Source.from(new ArrayList<>(mapToParts(ImmutableMap.of(
                        "userData", updatedData
                ))))).
                thenApply(WSResponse::asJson).
                thenApply(JsonHelper::jsonToMap);

        return updateDocument.thenCombine(updateMetaData, (doc, meta) ->  {

            val result = new HashMap<String, String>();

            result.putAll(doc);
            result.putAll(meta);

            return result;
        });
    }

    private WSRequest makeRequest(String operation, String onBehalfOfUser) {

        return wsClient.url(alfrescoUrl + "noms-spg/" + operation).
                addHeader("X-DocRepository-Remote-User", alfrescoUser).
                addHeader("X-DocRepository-Real-Remote-User", onBehalfOfUser);
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
