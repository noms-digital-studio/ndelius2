import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.google.inject.AbstractModule;
import injection.RestClientBuilderProvider;
import injection.RestHighLevelClientProvider;
import injection.DynamoClientProvider;
import interfaces.AnalyticsStore;
import interfaces.DocumentStore;
import interfaces.PdfGenerator;
import interfaces.OffenderSearch;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import services.AlfrescoStore;
import services.DynamoDbStore;
import services.RestPdfGenerator;
import services.search.ElasticOffenderSearch;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule {

    @Override
    public void configure() {

        bind(PdfGenerator.class).to(RestPdfGenerator.class);
        bind(DocumentStore.class).to(AlfrescoStore.class);
        bind(AnalyticsStore.class).to(DynamoDbStore.class);
        bind(OffenderSearch.class).to(ElasticOffenderSearch.class);

        bind(RestClientBuilder.class).toProvider(RestClientBuilderProvider.class);
        bind(RestHighLevelClient.class).toProvider(RestHighLevelClientProvider.class);


        bind(DynamoDB.class).toProvider(DynamoClientProvider.class).asEagerSingleton();
    }
}
