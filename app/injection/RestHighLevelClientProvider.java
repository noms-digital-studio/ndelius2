package injection;

import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import javax.inject.Inject;
import javax.inject.Provider;

public class RestHighLevelClientProvider implements Provider<RestHighLevelClient> {

    private final RestClientBuilder restClientBuilder;

    @Inject
    public RestHighLevelClientProvider(RestClientBuilder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    @Override
    public RestHighLevelClient get() {
        return new RestHighLevelClient(restClientBuilder);
    }
}
