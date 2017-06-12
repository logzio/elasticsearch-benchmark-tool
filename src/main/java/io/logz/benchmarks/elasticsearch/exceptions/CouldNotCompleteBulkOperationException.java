package io.logz.benchmarks.elasticsearch.exceptions;

/**
 * Created by roiravhon on 9/26/16.
 */
public class CouldNotCompleteBulkOperationException extends Exception {

    public CouldNotCompleteBulkOperationException() {
    }

    public CouldNotCompleteBulkOperationException(String message) {
        super(message);
    }

    public CouldNotCompleteBulkOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouldNotCompleteBulkOperationException(Throwable cause) {
        super(cause);
    }

    public CouldNotCompleteBulkOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
