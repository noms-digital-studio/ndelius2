package services;

import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import com.google.common.collect.ImmutableMap;
import interfaces.DocumentStore;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import play.Configuration;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;

import play.mvc.Http.MultipartFormData.*;

public class AlfrescoStore implements DocumentStore {

    private final String alfrescoUrl;
    private final String alfrescoUser;
    private final WSClient wsClient;

    @Inject
    public AlfrescoStore(Configuration configuration, WSClient wsClient) {

        alfrescoUrl = configuration.getString("store.alfresco.url");
        alfrescoUser = configuration.getString("store.alfresco.user");

        this.wsClient = wsClient;
    }

    @Override
    public CompletionStage<Map> uploadNewPdf(Byte[] document, String filename, String onBehalfOfUser, String crn, String author, Integer entityId) {

        try {

            val uploadFile = createTempFileName("upload", ".pdf");

            Files.write(uploadFile.toPath(), ArrayUtils.toPrimitive(document));

            val filePart = new FilePart("filedata", filename, "application/pdf", FileIO.fromPath(uploadFile.toPath()));
            val dataParts = ImmutableMap.of(
                    "CRN", crn,
                    "author", author,
                    "entityType", "OFFENDER",
                    "entityId", entityId.toString(),
                    "docType", "DOCUMENT"
            ).entrySet().stream().map(entry -> new DataPart(entry.getKey(), entry.getValue())).collect(Collectors.toList());

            val parts = new ArrayList<Part>();
            parts.add(filePart);
            parts.addAll(dataParts);

            Logger.info("Storing PDF: " + filename);

            return wsClient.url(alfrescoUrl + "noms-spg/uploadnew").
                    setHeader("X-DocRepository-Remote-User", alfrescoUser).
                    setHeader("X-DocRepository-Real-Remote-User", onBehalfOfUser).
                    post(Source.from(parts)).
                    thenApply(wsResponse -> {

                        val result = Json.fromJson(wsResponse.asJson(), Map.class);

                        Logger.info("Stored PDF: " + result);
                        return result;
                    });
        }
        catch (IOException ex) {

            Logger.error("Upload error", ex);

            return CompletableFuture.supplyAsync(() -> null);
        }
    }

    private File createTempFileName(String prefix, String suffix) throws IOException {

        val tempTile = File.createTempFile(prefix, suffix);

        tempTile.delete();
        return tempTile;
    }
}
