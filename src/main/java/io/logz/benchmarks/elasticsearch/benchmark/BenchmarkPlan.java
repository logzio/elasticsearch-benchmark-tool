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
    private BenchmarkStep currentStep;
    private boolean aborting = false;
    private boolean done = false;

    public BenchmarkPlan(ElasticsearchConfiguration esConfig) {
        benchmarkSteps = new LinkedList<>();
        esController = new ElasticsearchController(esConfig);
        generalMbean = GeneralMbean.getInstance();
    }

    public void addStep(BenchmarkStep step) {
        benchmarkSteps.add(step);
    }

    public void execute() {
        prepareForExecution();
        benchmarkSteps.forEach(benchmarkStep -> {
            if (!aborting) {
                currentStep = benchmarkStep;
                benchmarkStep.executeStep();
                generalMbean.incrementStep();
            }
        });

        done = true;
    }

    private void prepareForExecution() {
        esController.createIndex();
    }

    public void cleanupPlan() {
        esController.deleteIndex();
    }

    public void abortPlan() {
        if (!done) {
            aborting = true;
            if (currentStep != null)
                currentStep.abortStep();
        }
    }

    public ElasticsearchController getEsController() {
        return esController;
    }
}
