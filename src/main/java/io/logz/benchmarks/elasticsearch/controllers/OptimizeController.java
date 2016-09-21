package io.logz.benchmarks.elasticsearch.controllers;

import io.logz.benchmarks.elasticsearch.configuration.OptimizeConfiguration;
import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;

/**
 * Created by roiravhon on 9/19/16.
 */
public class OptimizeController implements BaseController {

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

    }

    @Override
    public void stop() {

    }
}
