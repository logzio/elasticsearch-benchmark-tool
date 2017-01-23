package io.logz.benchmarks.elasticsearch.controllers;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.logz.benchmarks.elasticsearch.configuration.ConfigurationParser;
import io.logz.benchmarks.elasticsearch.configuration.ElasticsearchConfiguration;
import io.logz.benchmarks.elasticsearch.configuration.IndexingConfiguration;
import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;
import io.logz.benchmarks.elasticsearch.exceptions.CouldNotCompleteBulkOperationException;
import io.logz.benchmarks.elasticsearch.metrics.IndexingMbean;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by roiravhon on 9/19/16.
 */
public class IndexingController implements BaseController {

    private static final Logger logger = LoggerFactory.getLogger(IndexingController.class);

    private final IndexingMbean indexingMbean;
    private final IndexingConfiguration configuration;
    private final ElasticsearchController esController;
    private final ExecutorService threadPool;

    public IndexingController(IndexingConfiguration configuration, ElasticsearchController esController) {

        this.configuration = configuration;
        this.esController = esController;
        indexingMbean = IndexingMbean.getInstance();

        // Creating the thread pool
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("indexing-thread-%d").build();
        threadPool = Executors.newFixedThreadPool(configuration.getNumberOfThreads(), namedThreadFactory);
    }

    @Override
    public String getControllerName() {
        return "Indexing";
    }

    @Override
    public void run() {

        // Creating indexing threads
        IntStream.range(0, configuration.getNumberOfThreads())
                .forEach((threadNumber) -> threadPool.submit(() -> startIndexing(threadNumber)));
    }

    @Override
    public void stop() {

        threadPool.shutdownNow();
    }

    private void startIndexing(int threadNumber) {

        try {
            logger.debug("Starting indexing thread #{}", threadNumber);

            while (true) {
                if (Thread.interrupted()) {
                    logger.debug("Got interrupt, stopping indexing thread #{}", threadNumber);
                    return;
                }

                // Create bulk
                Bulk currBulk = new Bulk.Builder()
                        .defaultIndex(esController.getIndexName())
                        .defaultType(esController.getDefaultType())
                        .addAction(esController.getMultipleDocuments(configuration.getBulkSize())
                                .stream()
                                .map((doc) -> new Index.Builder(doc).build())
                                .collect(Collectors.toList()))
                        .build();

                try {
                    // Lets index the bulk!
                    int numberOfFailedDocuments = esController.executeBulk(currBulk);

                    // Update metrics
                    indexingMbean.incrementSuccessfulDocuments(configuration.getBulkSize() - numberOfFailedDocuments);
                    indexingMbean.incrementFailedDocuements(numberOfFailedDocuments);

                } catch (CouldNotCompleteBulkOperationException e) {

                    // Assuming all documents failed..
                    indexingMbean.incrementFailedDocuements(configuration.getBulkSize());
                }
            }
        } catch (Throwable throwable) {
            logger.debug("Got unexpected exception while indexing!", throwable);
        }
    }
}
