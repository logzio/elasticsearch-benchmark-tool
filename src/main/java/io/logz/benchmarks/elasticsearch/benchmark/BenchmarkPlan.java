package io.logz.benchmarks.elasticsearch.benchmark;

import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by roiravhon on 9/19/16.
 */
public class BenchmarkPlan {

    private final List<BenchmarkStep> benchmarkSteps;

    public BenchmarkPlan() {
        benchmarkSteps = new LinkedList<>();
    }

    public void addStep(BenchmarkStep step) {
        benchmarkSteps.add(step);
    }

    public void execute(int indexingThreadCount, int searchThreadCount, ElasticsearchController esController) {
        benchmarkSteps.forEach(step -> step.executeStep(indexingThreadCount, searchThreadCount, esController));
    }
}
