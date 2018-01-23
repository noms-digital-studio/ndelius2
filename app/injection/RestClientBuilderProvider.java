package injection;

import com.typesafe.config.Config;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import javax.inject.Inject;
import javax.inject.Provider;

public class RestClientBuilderProvider implements Provider<RestClientBuilder> {

    private final String elasticSearchHost;
    private final String elasticSearchScheme;
    private final int elasticSearchPort;

    @Inject
    public RestClientBuilderProvider(Config configuration) {
        elasticSearchHost = configuration.getString("elastic.search.host");
        elasticSearchPort = configuration.getInt("elastic.search.port");
        elasticSearchScheme = configuration.getString("elastic.search.scheme");
    }

    @Override
    public RestClientBuilder get() {
        return RestClient.builder(new HttpHost(elasticSearchHost, elasticSearchPort, elasticSearchScheme));
    }
}
