package io.logz.benchmarks.elasticsearch.configuration;

import io.logz.benchmarks.elasticsearch.exceptions.InvalidConfigurationException;

/**
 * Created by roiravhon on 9/21/16.
 */
public class IndexingConfiguration implements BaseConfiguration{

    private static final int MAX_NUMBER_OF_THREADS = 10000;
    private static final int MIN_NUMBER_OF_THREADS = 1;
    private int numberOfThreads = 5;
    private int bulkSize = 1000;

    // For Jackson
    @SuppressWarnings("unused")
    public IndexingConfiguration() {

    }

    @SuppressWarnings("unused")
    public IndexingConfiguration(int numberOfThreads, int bulkSize) {
        this.numberOfThreads = numberOfThreads;
        this.bulkSize = bulkSize;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public int getBulkSize() {
        return bulkSize;
    }

    @Override
    public void validateConfig() throws InvalidConfigurationException {

        if (numberOfThreads < MIN_NUMBER_OF_THREADS || numberOfThreads >= MAX_NUMBER_OF_THREADS) {
            throw new InvalidConfigurationException("Indexing numberOfThreads must be between " + MIN_NUMBER_OF_THREADS + " and " + MAX_NUMBER_OF_THREADS);
        }

        if (bulkSize <= 0) {
            throw new InvalidConfigurationException("Bulk size must be more than 0! can't work with " + bulkSize);
        }
    }
}
