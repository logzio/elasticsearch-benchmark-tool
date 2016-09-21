package io.logz.benchmarks.elasticsearch.benchmark;

import io.logz.benchmarks.elasticsearch.configuration.ElasticsearchConfiguration;
import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;
import io.logz.benchmarks.elasticsearch.metrics.GeneralMbean;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by roiravhon on 9/19/16.
 */
public class BenchmarkPlan {

    private final List<BenchmarkStep> benchmarkSteps;
    private final ElasticsearchController esController;
    private final GeneralMbean generalMbean;

    public BenchmarkPlan(ElasticsearchConfiguration esConfig) {
        benchmarkSteps = new LinkedList<>();
        esController = new ElasticsearchController(esConfig);
        generalMbean = GeneralMbean.getInstance();
    }

    public void addStep(BenchmarkStep step) {
        benchmarkSteps.add(step);
    }

    public void execute() {
        benchmarkSteps.forEach(benchmarkStep -> {
            benchmarkStep.executeStep();
            generalMbean.incrementStep();
        });
    }

    public ElasticsearchController getEsController() {
        return esController;
    }
}
