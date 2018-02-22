package controllers;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import helpers.JsonHelper;
import interfaces.*;
import lombok.val;
import org.joda.time.DateTime;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Runtime.getRuntime;

public class UtilityController extends Controller {

    private final PdfGenerator pdfGenerator;
    private final DocumentStore documentStore;
    private final AnalyticsStore analyticsStore;
    private final OffenderSearch offenderSearch;
    private final OffenderApi offenderApi;

    private final boolean standaloneOperation;

    @Inject
    public UtilityController(Config configuration, PdfGenerator pdfGenerator,
                             DocumentStore documentStore,
                             AnalyticsStore analyticsStore,
                             OffenderSearch offenderSearch,
                             OffenderApi offenderApi) {

        standaloneOperation = configuration.getBoolean("standalone.operation");
        this.pdfGenerator = pdfGenerator;
        this.documentStore = documentStore;
        this.analyticsStore = analyticsStore;
        this.offenderSearch = offenderSearch;
        this.offenderApi = offenderApi;
    }

    public CompletionStage<Result> healthcheck() {
        val pdfGeneratorHealthFuture = pdfGenerator.isHealthy().toCompletableFuture();
        val documentStoreHealthFuture = documentStore.isHealthy().toCompletableFuture();
        val analyticsStoreHealthFuture = analyticsStore.isUp();
        val offenderSearchHealthFuture = offenderSearch.isHealthy().toCompletableFuture();
        val offenderApiFuture = offenderApi.isHealthy().toCompletableFuture();

        val allHealthFutures =
            CompletableFuture.allOf(pdfGeneratorHealthFuture,
                                    documentStoreHealthFuture,
                                    analyticsStoreHealthFuture,
                                    offenderSearchHealthFuture,
                                    offenderApiFuture);

        return allHealthFutures
            .thenApply(ignored -> buildResult(pdfGeneratorHealthFuture.join(),
                                              documentStoreHealthFuture.join(),
                                              analyticsStoreHealthFuture.join(),
                                              offenderSearchHealthFuture.join(),
                                              offenderApiFuture.join()));
    }

    public CompletionStage<Result> searchDb() {
        return offenderApi.searchDb(getQueryParams())
                .thenApply(JsonHelper::okJson);
    }

    public CompletionStage<Result> searchLdap() {
        return offenderApi.searchLdap(getQueryParams())
                .thenApply(JsonHelper::okJson);
    }

    private Map<String, String> getQueryParams() {
        return request().queryString().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));
    }

    private Result buildResult(Boolean pdfGeneratorStatus, Boolean documentStoreStatus,
                               Boolean analyticsStoreStatus, Boolean searchStatus, Boolean offenderApiStatus) {
        return JsonHelper.okJson(
            ImmutableMap.builder()
                .put("status", pdfGeneratorStatus &&
                               getDocumentStoreStatus(documentStoreStatus) &&
                               offenderApiStatus &&
                               searchStatus ? "OK" : "FAILED")
                .put("dateTime", DateTime.now().toString())
                .put("version", version())
                .put("runtime", runtimeInfo())
                .put("fileSystems", fileSystemDetails())
                .put("localHost", localhost())
                .put("dependencies", ImmutableMap.builder()
                    .put("pdf-generator", pdfGeneratorStatus ? "OK" : "FAILED")
                    .put("document-store", documentStoreStatus ? "OK" : "FAILED")
                    .put("analytics-store", analyticsStoreStatus ? "OK" : "FAILED")
                    .put("offender-search", searchStatus ? "OK" : "FAILED")
                    .put("offender-api", offenderApiStatus ? "OK" : "FAILED")
                    .build())
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

    private String version() {
        Package aPackage = UtilityController.class.getPackage();
        return aPackage != null && aPackage.getImplementationVersion() != null
            ? aPackage.getImplementationVersion() : "UNKNOWN";
    }
}
