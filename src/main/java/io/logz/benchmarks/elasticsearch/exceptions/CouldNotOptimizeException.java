package io.logz.benchmarks.elasticsearch.exceptions;

/**
 * Created by roiravhon on 9/27/16.
 */
public class CouldNotOptimizeException extends Exception {
    public CouldNotOptimizeException() {
    }

    public CouldNotOptimizeException(String message) {
        super(message);
    }

    public CouldNotOptimizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouldNotOptimizeException(Throwable cause) {
        super(cause);
    }

    public CouldNotOptimizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
