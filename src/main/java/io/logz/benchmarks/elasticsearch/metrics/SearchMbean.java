package io.logz.benchmarks.elasticsearch.metrics;

import com.udojava.jmx.wrapper.JMXBean;
import com.udojava.jmx.wrapper.JMXBeanAttribute;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by roiravhon on 9/20/16.
 */
@JMXBean(description = "Indexing JMX metrics")
public class SearchMbean {

    private static SearchMbean instance;
    private final AtomicLong numberOfSuccessfulSearches;

    public static SearchMbean getInstance() {
        if (instance == null) {
            instance = new SearchMbean();
        }

        return instance;
    }

    private SearchMbean() {
        numberOfSuccessfulSearches = new AtomicLong();
    }

    @JMXBeanAttribute(name = "numberOfSuccessfulSearches", description = "The accumulated number of successful documents searched")
    public long getNumberOfSuccessfulSearches() {
        return numberOfSuccessfulSearches.get();
    }

    public void incrementSuccessfulDocuments(long docCount) {
        numberOfSuccessfulSearches.addAndGet(docCount);
    }
}