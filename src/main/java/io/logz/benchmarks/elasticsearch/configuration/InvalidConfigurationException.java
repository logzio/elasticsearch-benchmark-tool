package io.logz.benchmarks.elasticsearch.configuration;

/**
 * Created by roiravhon on 9/21/16.
 */
public class InvalidConfigurationException extends Exception {

    public InvalidConfigurationException() {
    }

    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigurationException(Throwable cause) {
        super(cause);
    }

    public InvalidConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
