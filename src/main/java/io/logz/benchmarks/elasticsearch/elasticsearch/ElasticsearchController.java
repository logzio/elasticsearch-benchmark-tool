package io.logz.benchmarks.elasticsearch.elasticsearch;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.indices.CreateIndex;
import org.elasticsearch.common.settings.Settings;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by roiravhon on 9/19/16.
 */
public class ElasticsearchController {

    private static final int DEFAULT_NUMBER_OF_SHARDS = 1;
    private static final int DEFAULT_NUMBER_OF_REPLICAS = 0;
    private JestClient client;
    private String indexName;

    public ElasticsearchController(String elasticsearchAddress) {

        indexName = UUID.randomUUID().toString().substring(0, 8);

        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://" + elasticsearchAddress + ":9200")
                .multiThreaded(true)
                .build());
        client = factory.getObject();
    }

    public void createIndex() {

        Settings.Builder settingsBuilder = Settings.settingsBuilder();
        settingsBuilder.put("number_of_shards", DEFAULT_NUMBER_OF_SHARDS);
        settingsBuilder.put("number_of_replicas", DEFAULT_NUMBER_OF_REPLICAS);

        try {
            client.execute(new CreateIndex.Builder(indexName).settings(settingsBuilder.build().getAsMap()).build());

        } catch (IOException e) {
            throw new RuntimeException("Could not create index in elasticsearch!", e);
        }
    }

    public String getIndexName() {
        return indexName;
    }
}
