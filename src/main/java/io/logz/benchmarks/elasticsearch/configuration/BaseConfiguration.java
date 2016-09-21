package io.logz.benchmarks.elasticsearch.configuration;

/**
 * Created by roiravhon on 9/21/16.
 */
public interface BaseConfiguration {

    void validateConfig() throws InvalidConfigurationException;
}
