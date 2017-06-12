package io.logz.benchmarks.elasticsearch.benchmark;

import io.logz.benchmarks.elasticsearch.configuration.ElasticsearchConfiguration;
import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;
import io.logz.benchmarks.elasticsearch.metrics.GeneralMbean;
import io.logz.benchmarks.elasticsearch.metrics.IndexingMbean;
import io.logz.benchmarks.elasticsearch.metrics.SearchMbean;

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

    public void printStats() {

        IndexingMbean indexingMbean = IndexingMbean.getInstance();
        SearchMbean searchMbean = SearchMbean.getInstance();
        GeneralMbean generalMbean = GeneralMbean.getInstance();

        System.out.println("##############################################################################");
        System.out.println("");

        System.out.println("Benchmark statistics (" + generalMbean.getCurrStep() + " steps):");
        System.out.println("");

        System.out.println("\t Total documents successfully indexed: " + indexingMbean.getNumberOfSuccessfulDocumentsIndexed());
        System.out.println("\t Total documents that fail to index: " + indexingMbean.getNumberOfFailedDocumentsIndexed());
        System.out.println("");

        System.out.println("\t Total successful searches " + searchMbean.getNumberOfSuccessfulSearches());
        System.out.println("\t Total failed searches " + searchMbean.getNumberOfFailedSearches());
        System.out.println("\t Total documents fetched " + searchMbean.getNumberOfFetchedDocuments());
        System.out.println("\t Total successful query time, in MS " + searchMbean.getTotalSuccessfulSearchesTimeMs());
        System.out.println("\t Average successful query time, in MS " + searchMbean.getAverageSearchTimeMs());
        System.out.println("");

        System.out.println("");
        System.out.println("##############################################################################");
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

    private void prepareForExecution() {
        esController.createIndex();
    }
}
