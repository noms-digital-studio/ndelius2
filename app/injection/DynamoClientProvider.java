package injection;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.typesafe.config.Config;
import javax.inject.Inject;
import javax.inject.Provider;

public class DynamoClientProvider implements Provider<DynamoDB> {

    private final String dynamoDbUrl;

    @Inject
    public DynamoClientProvider(Config configuration) {

        dynamoDbUrl = configuration.getString("analytics.dynamo.connection");
    }

    @Override
    public DynamoDB get() {

        return new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient()); // dynamoDbUrl
    }
}
