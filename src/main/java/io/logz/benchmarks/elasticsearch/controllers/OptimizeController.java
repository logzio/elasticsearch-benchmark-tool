package io.logz.benchmarks.elasticsearch.controllers;

import io.logz.benchmarks.elasticsearch.configuration.OptimizeConfiguration;
import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;
import io.logz.benchmarks.elasticsearch.exceptions.CouldNotOptimizeException;
import io.searchbox.indices.ForceMerge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by roiravhon on 9/19/16.
 */
public class OptimizeController implements BaseController {

    private static final Logger logger = LoggerFactory.getLogger(OptimizeController.class);

    private final OptimizeConfiguration configuration;
    private final ElasticsearchController esController;

    public OptimizeController(OptimizeConfiguration configuration, ElasticsearchController esController) {

        this.configuration = configuration;
        this.esController = esController;
    }

    @Override
    public String getControllerName() {
        return "Optimize";
    }

    @Override
    public void run() {
        logger.debug("Starting to optimize! (force merge)");
        ForceMerge forceMerge = new ForceMerge.Builder()
                .maxNumSegments(configuration.getNumberOfSegments())
                .build();
        try {
            esController.executeForceMerge(forceMerge);
        } catch (CouldNotOptimizeException e) {
            logger.info("Could not optimize!", e);
        }
    }

    @Override
    public void stop() {
        // We cant cancel optimize..
    }
}