package injection;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import javax.inject.Inject;
import javax.inject.Provider;

public class DynamoClientProvider implements Provider<DynamoDB> {

    private final AmazonDynamoDB amazon;

    @Inject
    public DynamoClientProvider(AmazonDynamoDB amazon) {

        this.amazon = amazon;
    }

    @Override
    public DynamoDB get() {

        return new DynamoDB(amazon);
    }
}
