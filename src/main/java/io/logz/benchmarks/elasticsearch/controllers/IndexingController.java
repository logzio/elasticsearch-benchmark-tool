package io.logz.benchmarks.elasticsearch.controllers;

import io.logz.benchmarks.elasticsearch.configuration.ElasticsearchConfiguration;
import io.logz.benchmarks.elasticsearch.configuration.IndexingConfiguration;
import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;
import io.logz.benchmarks.elasticsearch.metrics.IndexingMbean;

/**
 * Created by roiravhon on 9/19/16.
 */
public class IndexingController implements BaseController {

    IndexingMbean indexingMbean;
    IndexingConfiguration configuration;
    ElasticsearchController esController;

    public IndexingController(IndexingConfiguration configuration, ElasticsearchController esController) {

        this.configuration = configuration;
        this.esController = esController;
        indexingMbean = IndexingMbean.getInstance();
    }

    @Override
    public String getControllerName() {
        return "Indexing";
    }

    @Override
    public void run() {


    }

    @Override
    public void stop() {

    }
}
