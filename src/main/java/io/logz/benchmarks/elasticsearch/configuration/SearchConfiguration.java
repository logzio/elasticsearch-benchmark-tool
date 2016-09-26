package io.logz.benchmarks.elasticsearch.configuration;

import io.logz.benchmarks.elasticsearch.exceptions.InvalidConfigurationException;

/**
 * Created by roiravhon on 9/21/16.
 */
public class SearchConfiguration implements BaseConfiguration {

    private static final int MIN_NUMBER_OF_THREADS = 1;
    private static final int MAX_NUMBER_OF_THREADS = 100;
    private static final int MIN_SEARCHES_PER_MINUTE = 1;
    private static final int MAX_SEARCHES_PER_MINUTE = 600;
    private int numberOfThreads = 5;
    private int searchesPerMinute = 60;

    // For Jackson
    @SuppressWarnings("unused")
    public SearchConfiguration() {

    }

    @SuppressWarnings("unused")
    public SearchConfiguration(int numberOfThreads, int searchesPerMinute) {
        this.numberOfThreads = numberOfThreads;
        this.searchesPerMinute = searchesPerMinute;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public int getSearchesPerMinute() {
        return searchesPerMinute;
    }

    @Override
    public void validateConfig() throws InvalidConfigurationException {

        if (numberOfThreads < MIN_NUMBER_OF_THREADS || numberOfThreads >= MAX_NUMBER_OF_THREADS) {
            throw new InvalidConfigurationException("Search numberOfThreads must be between " + MIN_NUMBER_OF_THREADS + " and " + MAX_NUMBER_OF_THREADS);
        }

        if (searchesPerMinute < MIN_SEARCHES_PER_MINUTE || searchesPerMinute >= MAX_SEARCHES_PER_MINUTE) {
            throw new InvalidConfigurationException("Search searchesPerMinute must be between " + MIN_SEARCHES_PER_MINUTE + " and " + MAX_SEARCHES_PER_MINUTE);
        }
    }
}
