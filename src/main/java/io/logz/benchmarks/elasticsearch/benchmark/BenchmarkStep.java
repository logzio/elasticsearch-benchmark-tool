package io.logz.benchmarks.elasticsearch.benchmark;

import io.logz.benchmarks.elasticsearch.configuration.ConfigurationParser;
import io.logz.benchmarks.elasticsearch.controllers.BaseController;
import io.logz.benchmarks.elasticsearch.controllers.IndexingController;
import io.logz.benchmarks.elasticsearch.controllers.SearchController;
import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by roiravhon on 9/19/16.
 */
public class BenchmarkStep {

    private final static Logger logger = LoggerFactory.getLogger(BenchmarkStep.class);
    private final List<BaseController> controllers;
    private final long durationMillis;

    public BenchmarkStep(List<BaseController> controllers, long durationMillis) {
        this.controllers = controllers;
        this.durationMillis = durationMillis;
    }

    public void executeStep(int indexingThreadCount, int searchThreadCount, ElasticsearchController esController) {

        //Executors.newFixedThreadPool(5);

        logger.info("Starting to execute step: " + toString());
        controllers.forEach(controller -> {

            if (controller instanceof IndexingController) {
                controller.run(indexingThreadCount, esController);

            } else if (controller instanceof SearchController) {
                controller.run(searchThreadCount, esController);

            } else {
                controller.run(1, esController);
            }
        });

        try {
            logger.info("Step started. Waiting for {} millis.", durationMillis);
            Thread.sleep(durationMillis);
            logger.info("Time out!");

        } catch (InterruptedException e) {
            logger.error("Got interrupted while running! gracefully shutting down.");

        } finally {
            logger.info("Stopping step: " + toString());
            controllers.forEach(BaseController::stop);
        }
    }

    @Override
    public String toString() {

        StringBuilder allControllersNames = new StringBuilder();
        controllers.forEach(controller -> allControllersNames.append(controller.getControllerName() + " "));

        return "Controllers " + allControllersNames.toString() + "run for " + durationMillis + " milliseconds";
    }
}
