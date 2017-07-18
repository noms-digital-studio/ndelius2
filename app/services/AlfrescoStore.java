package services;

import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import helpers.JsonHelper;
import interfaces.DocumentStore;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
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

import static helpers.JsonHelper.jsonToMap;

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
    public CompletionStage<Map<String, String>> uploadNewPdf(Byte[] document, String filename, String originalData, String onBehalfOfUser, String crn, Long entityId) {

        try {

            val uploadFile = createTempFileName("upload", ".pdf");

            Files.write(uploadFile.toPath(), ArrayUtils.toPrimitive(document));

            val filePart = new FilePart("filedata", filename, "application/pdf", FileIO.fromPath(uploadFile.toPath()));
            val dataParts = ImmutableMap.of(
                    "CRN", crn,
                    "author", onBehalfOfUser,
                    "entityType", "COURTREPORT",
                    "entityId", entityId.toString(),
                    "docType", "DOCUMENT"
                    //@TODO: Store originalData JSON string as document metadata
            ).entrySet().stream().map(entry -> new DataPart(entry.getKey(), entry.getValue())).collect(Collectors.toList());

            val parts = new ArrayList<Part>();
            parts.add(filePart);
            parts.addAll(dataParts);

            Logger.info("Storing PDF: " + filename);

            return makeRequest("uploadnew", onBehalfOfUser).
                    post(Source.from(parts)).
                    thenApply(wsResponse -> {

                        val result = jsonToMap(wsResponse.asJson());

                        Logger.info("Stored PDF: " + result);
                        return result;
                    });
        }
        catch (IOException ex) {

            Logger.error("Upload error", ex);

            return CompletableFuture.supplyAsync(ImmutableMap::of);
        }
    }

    @Override
    public CompletionStage<String> retrieveOriginalData(String documentId, String onBehalfOfUser) {

        return makeRequest("details/" + documentId, onBehalfOfUser).get().
                thenApply(WSResponse::asJson).
                thenApply(JsonHelper::jsonToMap).
                thenApply(result -> result.get("userData")); //@TODO: Retrieve originalData JSON string from Alfresco
    }

    private WSRequest makeRequest(String operation, String onBehalfOfUser) {

        return wsClient.url(alfrescoUrl + "noms-spg/" + operation).
                addHeader("X-DocRepository-Remote-User", alfrescoUser).
                addHeader("X-DocRepository-Real-Remote-User", onBehalfOfUser);
    }

    private static File createTempFileName(String prefix, String suffix) throws IOException {

        val tempTile = File.createTempFile(prefix, suffix);

        tempTile.delete();
        return tempTile;
    }
}
