package io.logz.benchmarks.elasticsearch.controllers;

import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;

/**
 * Created by roiravhon on 9/19/16.
 */
public interface BaseController {

    String getControllerName();
    void run(int numberOfThreads, ElasticsearchController esController);
    void stop();
}
