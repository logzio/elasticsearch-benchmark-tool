package io.logz.benchmarks.elasticsearch.configuration;

import io.logz.benchmarks.elasticsearch.exceptions.InvalidConfigurationException;

/**
 * Created by roiravhon on 9/21/16.
 */
public interface BaseConfiguration {

    void validateConfig() throws InvalidConfigurationException;
}
