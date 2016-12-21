package io.logz.benchmarks.elasticsearch.metrics;

import com.udojava.jmx.wrapper.JMXBean;
import com.udojava.jmx.wrapper.JMXBeanAttribute;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by roiravhon on 9/20/16.
 */
@JMXBean(description = "Search JMX metrics")
public class SearchMbean {

    private static SearchMbean instance;
    private final AtomicLong numberOfSuccessfulSearches;
    private final AtomicLong totalSuccessfulSearchesTimeMs;
    private final AtomicLong numberOfFailedSearches;
    private final AtomicLong numberOfFetchedDocuments;

    public static SearchMbean getInstance() {
        if (instance == null) {
            instance = new SearchMbean();
        }

        return instance;
    }

    private SearchMbean() {
        numberOfSuccessfulSearches = new AtomicLong();
        totalSuccessfulSearchesTimeMs = new AtomicLong();
        numberOfFailedSearches = new AtomicLong();
        numberOfFetchedDocuments = new AtomicLong();
    }

    @SuppressWarnings("unused")
    @JMXBeanAttribute(name = "numberOfSuccessfulSearches", description = "The accumulated number of successful documents searched")
    public long getNumberOfSuccessfulSearches() {
        return numberOfSuccessfulSearches.get();
    }

    @SuppressWarnings("unused")
    @JMXBeanAttribute(name = "totalSuccessfulSearchesTimeMs", description = "The total time all successful queries took, in MS")
    public long getTotalSuccessfulSearchesTimeMs() {
        return totalSuccessfulSearchesTimeMs.get();
    }

    @SuppressWarnings("unused")
    @JMXBeanAttribute(name = "averageSearchTimeMs", description = "The average time each query tool, in MS")
    public long getAverageSearchTimeMs() {
        if (getNumberOfSuccessfulSearches() > 0) {
            return getTotalSuccessfulSearchesTimeMs() / getNumberOfSuccessfulSearches();
        }
        return 0;
    }

    @SuppressWarnings("unused")
    @JMXBeanAttribute(name = "numberOfFailedSearches", description = "The accumulated number of failed searches")
    public long getNumberOfFailedSearches() {
        return numberOfFailedSearches.get();
    }

    @SuppressWarnings("unused")
    @JMXBeanAttribute(name = "numberOfFetchedDocuments", description = "The accumulated number of retrieved documents from searches")
    public long getNumberOfFetchedDocuments() {
        return numberOfFetchedDocuments.get();
    }

    public void incrementSuccessfulSearches() {
        numberOfSuccessfulSearches.incrementAndGet();
    }

    public void incrementTotalSearchTimeMs(long searchTime) {
        totalSuccessfulSearchesTimeMs.addAndGet(searchTime);
    }

    public void incrementNumberOfFailedSearches() {
        numberOfFailedSearches.incrementAndGet();
    }

    public void incrementNumberOfFetchedDocuments(long docCount) {
        numberOfFetchedDocuments.addAndGet(docCount);
    }
}