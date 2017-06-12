package io.logz.benchmarks.elasticsearch.controllers;

/**
 * Created by roiravhon on 9/19/16.
 */
public interface BaseController {

    String getControllerName();
    void run();
    void stop();
}
