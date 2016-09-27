package io.logz.benchmarks.elasticsearch.exceptions;

/**
 * Created by roiravhon on 9/27/16.
 */
public class CouldNotExecuteSearchException extends Exception {
    public CouldNotExecuteSearchException() {
    }

    public CouldNotExecuteSearchException(String message) {
        super(message);
    }

    public CouldNotExecuteSearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouldNotExecuteSearchException(Throwable cause) {
        super(cause);
    }

    public CouldNotExecuteSearchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
