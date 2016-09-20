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

    public static IndexingMbean getInstance() {
        if (instance == null) {
            instance = new IndexingMbean();
        }

        return instance;
    }

    private IndexingMbean() {
        numberOfSuccessfulDocumentsIndexed = new AtomicLong();
    }

    @JMXBeanAttribute(name = "numberOfSuccessfulDocumentsIndexed", description = "The accumulated number of successful documents indexed")
    public long getNumberOfSuccessfulDocumentsIndexed() {
        return numberOfSuccessfulDocumentsIndexed.get();
    }

    public void incrementSuccessfulDocuments(long docCount) {
        numberOfSuccessfulDocumentsIndexed.addAndGet(docCount);
    }
}