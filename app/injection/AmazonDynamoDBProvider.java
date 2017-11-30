package injection;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import javax.inject.Provider;

public class AmazonDynamoDBProvider implements Provider<AmazonDynamoDB> {

    @Override
    public AmazonDynamoDB get() {

        // this requires a default region to run e.g AWS_REGION environment variable set or aws config containing a default region
        // for instance in Circle CI this is set to a random AWS region just so factory function does not fail
        return AmazonDynamoDBClientBuilder.defaultClient();


        // use configuration below  when running locally against DynamoDB - NB region is not important
//        return AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
//                new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2")
//        ).build();
    }
}
