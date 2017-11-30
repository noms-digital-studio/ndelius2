package injection;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import javax.inject.Provider;

public class AmazonDynamoDBProvider implements Provider<AmazonDynamoDB> {

    @Override
    public AmazonDynamoDB get() {

        return AmazonDynamoDBClientBuilder.defaultClient();
//        return AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
//                new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2")
//        ).build();
    }
}
