package services.helpers;

import interfaces.HealthCheckResult;
import lombok.val;
import org.bson.Document;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import com.mongodb.rx.client.MongoDatabase;

import static interfaces.HealthCheckResult.unhealthy;

public class MongoUtils {

    public static CompletableFuture<HealthCheckResult> isHealthy(MongoDatabase database) {
        val result = new CompletableFuture<HealthCheckResult>();

        database.runCommand(new Document("dbStats", 1))
            .timeout(5000, TimeUnit.MILLISECONDS)
            .map(document -> new HealthCheckResult(document.get("ok").equals(1.0), document))
            .onErrorReturn(error -> {
                val healthCheckResult = unhealthy(error.getLocalizedMessage());
                result.complete(healthCheckResult);
                return null;
            })
            .subscribe(result::complete);

        return result;
    }
}
