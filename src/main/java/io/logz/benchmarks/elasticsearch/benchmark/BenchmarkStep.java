package io.logz.benchmarks.elasticsearch.benchmark;

import io.logz.benchmarks.elasticsearch.controllers.BaseController;
import io.logz.benchmarks.elasticsearch.controllers.IndexingController;
import io.logz.benchmarks.elasticsearch.controllers.SearchController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by roiravhon on 9/19/16.
 */
public class BenchmarkStep {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkStep.class);
    private final List<BaseController> controllers;
    private final long durationMillis;

    public BenchmarkStep(long durationMillis) {
        this.durationMillis = durationMillis;
        controllers = new LinkedList<>();
    }

    public void addController(BaseController controller) {
        controllers.add(controller);
    }

    public void executeStep() {

        logger.info("Starting to execute step: " + toString());
        controllers.forEach(BaseController::run);

        try {
            logger.info("Step started. Waiting for {} millis.", durationMillis);
            Thread.sleep(durationMillis);
            logger.info("Time out!");

        } catch (InterruptedException e) {
            logger.error("Got interrupted while running! gracefully shutting down.");

        } finally {
            logger.info("Stopping step: {}", toString());
            controllers.forEach(BaseController::stop);
        }
    }

    public void abortStep() {
        logger.info("Aborting step: {}", toString());
        controllers.forEach(BaseController::stop);
    }

    @Override
    public String toString() {

        StringBuilder allControllersNames = new StringBuilder();
        controllers.forEach(controller -> allControllersNames.append(controller.getControllerName()).append(", "));

        return "Controllers: " + allControllersNames.toString() + "for: " + durationMillis + " ms";
    }
}
