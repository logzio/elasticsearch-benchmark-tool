package io.logz.benchmarks.elasticsearch.controllers;

import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;
import io.logz.benchmarks.elasticsearch.metrics.IndexingMbean;

/**
 * Created by roiravhon on 9/19/16.
 */
public class IndexingController implements BaseController {

    IndexingMbean indexingMbean;

    public IndexingController() {

        indexingMbean = IndexingMbean.getInstance();
    }

    @Override
    public String getControllerName() {
        return null;
    }

    @Override
    public void run(int numberOfThreads, ElasticsearchController esController) {

        
    }

    @Override
    public void stop() {

    }
}
