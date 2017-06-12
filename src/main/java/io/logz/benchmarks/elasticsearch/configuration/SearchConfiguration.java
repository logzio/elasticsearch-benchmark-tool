package io.logz.benchmarks.elasticsearch.configuration;

import io.logz.benchmarks.elasticsearch.exceptions.InvalidConfigurationException;

/**
 * Created by roiravhon on 9/21/16.
 */
public class SearchConfiguration implements BaseConfiguration {

    private static final int MIN_NUMBER_OF_THREADS = 1;
    private static final int MAX_NUMBER_OF_THREADS = 10000;
    private int numberOfThreads = 5;

    // For Jackson
    @SuppressWarnings("unused")
    public SearchConfiguration() {

    }

    @SuppressWarnings("unused")
    public SearchConfiguration(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    @Override
    public void validateConfig() throws InvalidConfigurationException {

        if (numberOfThreads < MIN_NUMBER_OF_THREADS || numberOfThreads >= MAX_NUMBER_OF_THREADS) {
            throw new InvalidConfigurationException("Search numberOfThreads must be between " + MIN_NUMBER_OF_THREADS + " and " + MAX_NUMBER_OF_THREADS);
        }
    }
}
