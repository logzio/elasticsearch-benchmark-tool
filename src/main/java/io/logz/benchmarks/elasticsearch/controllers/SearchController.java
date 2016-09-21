package io.logz.benchmarks.elasticsearch.controllers;

import io.logz.benchmarks.elasticsearch.configuration.SearchConfiguration;
import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;

/**
 * Created by roiravhon on 9/19/16.
 */
public class SearchController implements BaseController {

    private final SearchConfiguration configuration;
    private final ElasticsearchController esController;

    public SearchController(SearchConfiguration configuration, ElasticsearchController esController) {

        this.configuration = configuration;
        this.esController = esController;
    }

    @Override
    public String getControllerName() {
        return "Search";
    }

    @Override
    public void run() {

    }

    @Override
    public void stop() {

    }
}
