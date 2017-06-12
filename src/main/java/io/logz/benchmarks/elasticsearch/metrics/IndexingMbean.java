package io.logz.benchmarks.elasticsearch.metrics;

import com.udojava.jmx.wrapper.JMXBean;
import com.udojava.jmx.wrapper.JMXBeanAttribute;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by roiravhon on 9/20/16.
 */
@JMXBean(description = "Indexing JMX metrics")
public class IndexingMbean {

    private static IndexingMbean instance;
    private final AtomicLong numberOfSuccessfulDocumentsIndexed;
    private final AtomicLong numberOfFailedDocumentsIndexed;

    public static IndexingMbean getInstance() {
        if (instance == null) {
            instance = new IndexingMbean();
        }

        return instance;
    }

    private IndexingMbean() {
        numberOfSuccessfulDocumentsIndexed = new AtomicLong();
        numberOfFailedDocumentsIndexed = new AtomicLong();
    }

    @SuppressWarnings("unused")
    @JMXBeanAttribute(name = "numberOfSuccessfulDocumentsIndexed", description = "The accumulated number of successful documents indexed")
    public long getNumberOfSuccessfulDocumentsIndexed() {
        return numberOfSuccessfulDocumentsIndexed.get();
    }

    @SuppressWarnings("unused")
    @JMXBeanAttribute(name = "numberOfFailedDocumentsIndexed", description = "The accumulated number of failed documents indexed")
    public long getNumberOfFailedDocumentsIndexed() {
        return numberOfFailedDocumentsIndexed.get();
    }

    public void incrementSuccessfulDocuments(long docCount) {
        numberOfSuccessfulDocumentsIndexed.addAndGet(docCount);
    }

    public void incrementFailedDocuements(long docCount) {
        numberOfFailedDocumentsIndexed.addAndGet(docCount);
    }
}