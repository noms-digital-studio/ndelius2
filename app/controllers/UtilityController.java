package controllers;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import helpers.JsonHelper;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import lombok.val;
import org.joda.time.DateTime;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import static java.lang.Runtime.getRuntime;

public class UtilityController extends Controller {

    private final String version;

    private final PdfGenerator pdfGenerator;
    private final DocumentStore documentStore;
    private final AnalyticsStore analyticsStore;

    private final boolean standaloneOperation;

    @Inject
    public UtilityController(Config configuration, PdfGenerator pdfGenerator,
                             DocumentStore documentStore,
                             AnalyticsStore analyticsStore) {

        version = configuration.getString("app.version");
        standaloneOperation = configuration.getBoolean("standalone.operation");
        this.pdfGenerator = pdfGenerator;
        this.documentStore = documentStore;
        this.analyticsStore = analyticsStore;
    }

    public CompletionStage<Result> healthcheck() {
        val pdfGeneratorHealthFuture = pdfGenerator.isHealthy().toCompletableFuture();
        val documentGeneratorHealthFuture = documentStore.isHealthy().toCompletableFuture();
        val analyticsStoreHealthFuture = analyticsStore.isUp();

        val allHealthFutures =
            CompletableFuture.allOf(pdfGeneratorHealthFuture,
                                    documentGeneratorHealthFuture,
                                    analyticsStoreHealthFuture);

        return allHealthFutures
            .thenApply(ignored -> buildResult(pdfGeneratorHealthFuture.join(),
                                              documentGeneratorHealthFuture.join(),
                                              analyticsStoreHealthFuture.join()));
    }

    private Result buildResult(Boolean pdfGeneratorStatus, Boolean documentStoreStatus, Boolean analyticsStoreStatus) {
        return JsonHelper.okJson(
            ImmutableMap.builder()
                .put("status", pdfGeneratorStatus && getDocumentStoreStatus(documentStoreStatus) ? "OK" : "FAILED")
                .put("dateTime", DateTime.now().toString())
                .put("version", version)
                .put("runtime", runtimeInfo())
                .put("fileSystems", fileSystemDetails())
                .put("localHost", localhost())
                .put("dependencies", ImmutableMap.of(
                    "pdf-generator", pdfGeneratorStatus ? "OK" : "FAILED",
                    "document-store", documentStoreStatus ? "OK" : "FAILED",
                    "analytics-store", analyticsStoreStatus ? "OK" : "FAILED"))
                .build());
    }

    private Boolean getDocumentStoreStatus(Boolean documentStoreStatus) {
        return documentStoreStatus || standaloneOperation;
    }

    private ImmutableMap<String, ? extends Number> runtimeInfo() {
        return ImmutableMap.of(
                "processors", getRuntime().availableProcessors(),
                "freeMemory", getRuntime().freeMemory(),
                "totalMemory", getRuntime().totalMemory(),
                "maxMemory", getRuntime().maxMemory()
        );
    }

    private String localhost() {
        String localHost;

        try {
            localHost = InetAddress.getLocalHost().toString();

        } catch (UnknownHostException ex) {

            localHost = "unknown";
        }

        return localHost;
    }

    private Stream<Object> fileSystemDetails() {
        return Arrays.stream(File.listRoots()).map(root -> ImmutableMap.of(
                    "filePath", root.getAbsolutePath(),
                    "totalSpace", root.getTotalSpace(),
                    "freeSpace", root.getFreeSpace(),
                    "usableSpace", root.getUsableSpace()
            ));
    }
}
