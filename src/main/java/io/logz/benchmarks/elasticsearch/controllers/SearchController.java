package io.logz.benchmarks.elasticsearch.controllers;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.logz.benchmarks.elasticsearch.configuration.SearchConfiguration;
import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;
import io.logz.benchmarks.elasticsearch.exceptions.CouldNotExecuteSearchException;
import io.logz.benchmarks.elasticsearch.metrics.SearchMbean;
import io.searchbox.core.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by roiravhon on 9/19/16.
 */
public class SearchController implements BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private final SearchConfiguration configuration;
    private final ElasticsearchController esController;
    private final SearchMbean searchMbean;
    private final ScheduledExecutorService executorService;

    public SearchController(SearchConfiguration configuration, ElasticsearchController esController) {

        this.configuration = configuration;
        this.esController = esController;

        searchMbean = SearchMbean.getInstance();

        // Creating the executor service
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("search-thread-%d").build();
        executorService = new ScheduledThreadPoolExecutor(configuration.getNumberOfThreads(), namedThreadFactory);
    }

    @Override
    public String getControllerName() {
        return "Search";
    }

    @Override
    public void run() {

        double searchRateMillis = (60 / (double)configuration.getSearchesPerMinute()) * 1000;
        executorService.scheduleAtFixedRate(this::startSearching, 0, (int)searchRateMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {

        executorService.shutdownNow();
    }

    @SuppressWarnings("WeakerAccess")
    public void startSearching() {

        if (Thread.interrupted())
            return;

        String currSearch = esController.getSearch();

        Search search = new Search.Builder(currSearch)
                .addIndex(esController.getIndexName())
                .addType(esController.getDefaultType())
                .build();

        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            int docCount = esController.executeSearch(search);
            stopwatch.stop();

            searchMbean.incrementSuccessfulSearches();
            searchMbean.incrementNumberOfFetchedDocuments(docCount);
            searchMbean.incrementTotalSearchTimeMs(stopwatch.elapsed(TimeUnit.MILLISECONDS));

        } catch (CouldNotExecuteSearchException e) {
            stopwatch.stop();
            logger.debug("Could not execute search", e);
            searchMbean.incrementNumberOfFailedSearches();
        }
    }
}
