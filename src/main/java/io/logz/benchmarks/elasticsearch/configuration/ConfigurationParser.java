package io.logz.benchmarks.elasticsearch.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigObject;
import io.logz.benchmarks.elasticsearch.benchmark.BenchmarkPlan;
import io.logz.benchmarks.elasticsearch.benchmark.BenchmarkStep;
import io.logz.benchmarks.elasticsearch.controllers.IndexingController;
import io.logz.benchmarks.elasticsearch.controllers.NoopController;
import io.logz.benchmarks.elasticsearch.controllers.OptimizeController;
import io.logz.benchmarks.elasticsearch.controllers.SearchController;
import io.logz.benchmarks.elasticsearch.exceptions.InvalidConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Created by roiravhon on 9/19/16.
 */
public class ConfigurationParser {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationParser.class);

    public static BenchmarkPlan parseConfiguration(String configurationFile) throws InvalidConfigurationException {
        try {
            Config config = ConfigFactory.parseFile(new File(configurationFile));
            ConfigObject elasticsearchConfig = config.getObject("elasticsearch");
            List<? extends ConfigObject> stepsConfig = config.getObjectList("steps");

            ObjectMapper objectMapper = new ObjectMapper();

            // Parse elasticsearch configuration
            ElasticsearchConfiguration esConfig = objectMapper.convertValue(elasticsearchConfig.unwrapped(), ElasticsearchConfiguration.class);
            esConfig.validateConfig();

            // Build initial benchmark plan
            BenchmarkPlan benchmarkPlan = new BenchmarkPlan(esConfig);

            for (ConfigObject currConfigObject: stepsConfig) {

                Config currConfig = currConfigObject.toConfig();

                long duration = currConfig.getDuration("duration").toMillis();
                BenchmarkStep step = new BenchmarkStep(duration);

                if (currConfig.hasPath("indexing")) {
                    IndexingConfiguration indexingConfig = objectMapper.convertValue(currConfig.getObject("indexing").unwrapped(), IndexingConfiguration.class);
                    indexingConfig.validateConfig();
                    step.addController(new IndexingController(indexingConfig, benchmarkPlan.getEsController()));
                }

                if (currConfig.hasPath("search")) {
                    SearchConfiguration searchConfig = objectMapper.convertValue(currConfig.getObject("search").unwrapped(), SearchConfiguration.class);
                    searchConfig.validateConfig();
                    step.addController(new SearchController(searchConfig, benchmarkPlan.getEsController()));
                }

                if (currConfig.hasPath("optimize")) {
                    OptimizeConfiguration optimizeConfig = objectMapper.convertValue(currConfig.getObject("optimize").unwrapped(), OptimizeConfiguration.class);
                    optimizeConfig.validateConfig();
                    step.addController(new OptimizeController(optimizeConfig, benchmarkPlan.getEsController()));
                }

                if (currConfig.hasPath("noop")) {
                    step.addController(new NoopController());
                }

                // Adding the final step
                benchmarkPlan.addStep(step);
            }

            return benchmarkPlan;

        } catch (ConfigException e) {
            throw new RuntimeException("Could not parse configuration file!", e);
        }
    }
}
