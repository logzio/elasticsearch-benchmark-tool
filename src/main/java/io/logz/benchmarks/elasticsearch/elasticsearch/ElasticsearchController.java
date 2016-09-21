package io.logz.benchmarks.elasticsearch.elasticsearch;

import io.logz.benchmarks.elasticsearch.configuration.ElasticsearchConfiguration;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import org.elasticsearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by roiravhon on 9/19/16.
 */
public class ElasticsearchController {

    private final static Logger logger = LoggerFactory.getLogger(ElasticsearchController.class);
    private final ElasticsearchConfiguration esConfig;
    private JestClient client;
    private String indexName;

    public ElasticsearchController(ElasticsearchConfiguration esConfig) {

        this.esConfig = esConfig;

        indexName = UUID.randomUUID().toString().substring(0, 8);

        logger.info("Random test index name set to: {}", indexName);

        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(esConfig.getElasticsearchProtocol() + "://" + esConfig.getElasticsearchAddress() + ":" + esConfig.getElasticsearchPort())
                .multiThreaded(true)
                .build());

        logger.info("Creating Jest client to handle all ES operations");
        client = factory.getObject();
    }

    public void createIndex() {

        Settings.Builder settingsBuilder = Settings.settingsBuilder();
        settingsBuilder.put("number_of_shards", esConfig.getNumberOfShards());
        settingsBuilder.put("number_of_replicas", esConfig.getNumberOfReplicas());

        try {
            logger.info("Creating test index on ES, named {} with {} shards and {} replicas", indexName, esConfig.getNumberOfShards(), esConfig.getNumberOfReplicas());
            client.execute(new CreateIndex.Builder(indexName).settings(settingsBuilder.build().getAsMap()).build());

        } catch (IOException e) {
            throw new RuntimeException("Could not create index in elasticsearch!", e);
        }
    }

    public void deleteIndex() {

        try {
            logger.info("Deleting test index {} from ES", indexName);
            client.execute(new DeleteIndex.Builder(indexName).build());

        } catch (IOException e) {
            throw new RuntimeException("Could not delete index from ES!");
        }
    }

    public String getIndexName() {
        return indexName;
    }
}
