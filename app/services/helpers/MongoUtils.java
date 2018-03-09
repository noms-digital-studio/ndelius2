package services.helpers;

import lombok.val;
import org.bson.Document;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import com.mongodb.rx.client.MongoDatabase;

public class MongoUtils {

    public static CompletableFuture<Boolean> isHealthy(MongoDatabase database) {
        val result = new CompletableFuture<Boolean>();

        database.runCommand(new Document("dbStats", 1))
            .timeout(5000, TimeUnit.MILLISECONDS)
            .map(document -> document.get("ok").equals(1.0))
            .onErrorReturn(ignored -> result.complete(false))
            .subscribe(result::complete);

        return result;
    }
}
