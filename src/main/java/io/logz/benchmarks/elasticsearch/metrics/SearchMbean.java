package io.logz.benchmarks.elasticsearch.metrics;

import com.udojava.jmx.wrapper.JMXBean;
import com.udojava.jmx.wrapper.JMXBeanAttribute;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by roiravhon on 9/20/16.
 */
@JMXBean(description = "Search JMX metrics")
public class SearchMbean {

    private static SearchMbean instance;
    private final AtomicLong numberOfSuccessfulSearches;
    private final AtomicLong totalSearchesTimeMs;

    public static SearchMbean getInstance() {
        if (instance == null) {
            instance = new SearchMbean();
        }

        return instance;
    }

    private SearchMbean() {
        numberOfSuccessfulSearches = new AtomicLong();
        totalSearchesTimeMs = new AtomicLong();
    }

    @SuppressWarnings("unused")
    @JMXBeanAttribute(name = "numberOfSuccessfulSearches", description = "The accumulated number of successful documents searched")
    public long getNumberOfSuccessfulSearches() {
        return numberOfSuccessfulSearches.get();
    }

    @SuppressWarnings("unused")
    @JMXBeanAttribute(name = "totalQueryTimeMs", description = "The total time all queries took, in MS")
    public long getTotalSearchesTimeMs() {
        return numberOfSuccessfulSearches.get();
    }

    @SuppressWarnings("unused")
    @JMXBeanAttribute(name = "averageSearchTimeMs", description = "The average time each query tool, in MS")
    public long getAverageSearchTimeMs() {
        return getTotalSearchesTimeMs() / getNumberOfSuccessfulSearches();
    }

    public void incrementSuccessfulSearches(long docCount) {
        numberOfSuccessfulSearches.addAndGet(docCount);
    }

    public void incrementTotalSearchTimeMs(long searchTime) {
        totalSearchesTimeMs.addAndGet(searchTime);
    }
}