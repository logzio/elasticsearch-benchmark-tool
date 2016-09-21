package io.logz.benchmarks.elasticsearch.configuration;

/**
 * Created by roiravhon on 9/21/16.
 */
public class IndexingConfiguration implements BaseConfiguration{

    private static final int MAX_NUMBER_OF_THREADS = 100;
    private static final int MIN_NUMBER_OF_THREADS = 1;
    private int numberOfThreads = 5;

    // For Jackson
    @SuppressWarnings("unused")
    public IndexingConfiguration() {

    }

    @SuppressWarnings("unused")
    public IndexingConfiguration(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    @Override
    public void validateConfig() throws InvalidConfigurationException {

        if (numberOfThreads < MIN_NUMBER_OF_THREADS || numberOfThreads >= MAX_NUMBER_OF_THREADS) {
            throw new InvalidConfigurationException("Indexing numberOfThreads must be between " + MIN_NUMBER_OF_THREADS + " and " + MAX_NUMBER_OF_THREADS);
        }
    }
}
