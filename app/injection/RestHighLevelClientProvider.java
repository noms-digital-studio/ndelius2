package injection;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.typesafe.config.Config;
import lombok.val;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import javax.inject.Inject;
import javax.inject.Provider;

public class RestHighLevelClientProvider implements Provider<RestHighLevelClient> {

    private static final AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();
    private final RestClientBuilder restClientBuilder;
    private final String awsRegion;
    private final String awsServiceName;
    private final boolean shouldSignRequests;
    @Inject
    public RestHighLevelClientProvider(Config configuration, RestClientBuilder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
        this.awsRegion = configuration.getString("elastic.search.aws.region");
        this.awsServiceName = configuration.getString("elastic.search.aws.servicename");
        this.shouldSignRequests = configuration.getBoolean("elastic.search.aws.signrequests");
    }

    @Override
    public RestHighLevelClient get() {
        if (shouldSignRequests) {
            val signer = new AWS4Signer();
            signer.setServiceName(awsServiceName);
            signer.setRegionName(awsRegion);

            return new RestHighLevelClient(restClientBuilder.setHttpClientConfigCallback(
                    callback -> callback.addInterceptorLast(
                            new AWSRequestSigningApacheInterceptor(awsServiceName, signer, credentialsProvider))));
        }
        return new RestHighLevelClient(restClientBuilder);
    }
}
