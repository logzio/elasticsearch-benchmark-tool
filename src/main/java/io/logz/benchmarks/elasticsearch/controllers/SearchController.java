package io.logz.benchmarks.elasticsearch.controllers;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.logz.benchmarks.elasticsearch.configuration.SearchConfiguration;
import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

/**
 * Created by roiravhon on 9/19/16.
 */
public class SearchController implements BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    private final SearchConfiguration configuration;
    private final ElasticsearchController esController;
    private final ExecutorService threadPool;

    public SearchController(SearchConfiguration configuration, ElasticsearchController esController) {

        this.configuration = configuration;
        this.esController = esController;

        // Creating the thread pool
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("search-thread-%d").build();
        threadPool = Executors.newFixedThreadPool(5, namedThreadFactory);
    }

    @Override
    public String getControllerName() {
        return "Search";
    }

    @Override
    public void run() {

        // Creating indexing threads
        IntStream.range(0, configuration.getNumberOfThreads())
                .forEach((threadNumber) -> threadPool.submit(() -> startSearching(threadNumber)));
    }

    @Override
    public void stop() {

        threadPool.shutdownNow();
    }

    private void startSearching(int threadNumber) {

        logger.debug("Starting search thread #{}", threadNumber);

        while (true) {
            if (Thread.interrupted()) {
                logger.debug("Got interrupt, stopping search thread #{}", threadNumber);
                return;
            }

            // Round robin on searches

            // Start stopwatch

            // Execute search

            // Stop stopwatch

            // Update metrics
        }
    }
}
