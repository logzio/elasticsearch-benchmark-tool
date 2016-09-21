package io.logz.benchmarks.elasticsearch.configuration;

/**
 * Created by roiravhon on 9/21/16.
 */
public class ElasticsearchConfiguration implements BaseConfiguration {

    private String elasticsearchAddress;
    private String elasticsearchProtocol = "http";
    private int elasticsearchPort = 9200;
    private int numberOfShards = 1;
    private int numberOfReplicas = 0;

    // For Jackson
    @SuppressWarnings("unused")
    public ElasticsearchConfiguration() {

    }

    @SuppressWarnings("unused")
    public ElasticsearchConfiguration(String elasticsearchAddress, String elasticsearchProtocol, Integer elasticsearchPort, Integer numberOfShards, Integer numberOfReplicas) {
        this.elasticsearchAddress = elasticsearchAddress;
        this.elasticsearchProtocol = elasticsearchProtocol;
        this.elasticsearchPort = elasticsearchPort;
        this.numberOfShards = numberOfShards;
        this.numberOfReplicas = numberOfReplicas;
    }

    public String getElasticsearchAddress() {
        return elasticsearchAddress;
    }

    public String getElasticsearchProtocol() {
        return elasticsearchProtocol;
    }

    public int getElasticsearchPort() {
        return elasticsearchPort;
    }

    public int getNumberOfShards() {
        return numberOfShards;
    }

    public int getNumberOfReplicas() {
        return numberOfReplicas;
    }

    @Override
    public void validateConfig() throws InvalidConfigurationException {

        if (elasticsearchAddress == null || elasticsearchAddress == "") {
            throw new InvalidConfigurationException("elasticsearchAddress must be set!");
        }

        if (!elasticsearchProtocol.equals("http")) {
            throw new InvalidConfigurationException("Only supporting http protocol for Elasticsearch for now");
        }
    }
}
