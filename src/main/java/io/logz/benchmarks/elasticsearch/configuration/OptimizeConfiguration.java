package io.logz.benchmarks.elasticsearch.configuration;

/**
 * Created by roiravhon on 9/21/16.
 */
public class OptimizeConfiguration implements BaseConfiguration {

    private static final int MIN_NUMBER_OF_SEGMENTS = 1;
    private static final int MAX_NUMBER_OF_SEGMENTS = 10;
    private int numberOfSegments = 1;

    // For Jackson
    @SuppressWarnings("unused")
    public OptimizeConfiguration() {

    }

    @SuppressWarnings("unused")
    public OptimizeConfiguration(int maxNumberOfSegments) {
        this.numberOfSegments = maxNumberOfSegments;
    }

    public int getNumberOfSegments() {
        return numberOfSegments;
    }

    @Override
    public void validateConfig() throws InvalidConfigurationException {
        if (numberOfSegments < MIN_NUMBER_OF_SEGMENTS || numberOfSegments >= MAX_NUMBER_OF_SEGMENTS) {
            throw new InvalidConfigurationException("Optimize numberOfSegments must be between " + MIN_NUMBER_OF_SEGMENTS + " and " + MAX_NUMBER_OF_SEGMENTS);
        }
    }
}
