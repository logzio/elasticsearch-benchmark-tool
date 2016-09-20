package io.logz.benchmarks.elasticsearch.controllers;

import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;

/**
 * Created by roiravhon on 9/19/16.
 */
public class SearchController implements BaseController {

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
