package io.logz.benchmarks.elasticsearch.controllers;

/**
 * Created by roiravhon on 9/19/16.
 */
public class NoopController implements BaseController{

    @Override
    public String getControllerName() {
        return "Noop";
    }

    @Override
    public void run() {

    }

    @Override
    public void stop() {

    }
}
