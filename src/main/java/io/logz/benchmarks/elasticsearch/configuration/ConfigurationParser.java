package io.logz.benchmarks.elasticsearch.configuration;

import com.google.common.base.Splitter;
import com.google.common.io.Files;
import io.logz.benchmarks.elasticsearch.benchmark.BenchmarkPlan;
import io.logz.benchmarks.elasticsearch.benchmark.BenchmarkStep;
import io.logz.benchmarks.elasticsearch.controllers.BaseController;
import io.logz.benchmarks.elasticsearch.controllers.IndexingController;
import io.logz.benchmarks.elasticsearch.controllers.NoopController;
import io.logz.benchmarks.elasticsearch.controllers.OptimizeController;
import io.logz.benchmarks.elasticsearch.controllers.SearchController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roiravhon on 9/19/16.
 */
public class ConfigurationParser {

    private final static Logger logger = LoggerFactory.getLogger(ConfigurationParser.class);

    public static BenchmarkPlan parseConfiguration(String configurationFile) {

        try {
            logger.info("Trying to parse configuration file {}", configurationFile);

            BenchmarkPlan plan = new BenchmarkPlan();
            Files.readLines(new File(configurationFile), Charset.forName("utf-8")).forEach(line -> {

                String[] configurationLine = line.split("=");
                List<BaseController> controllers = new ArrayList<>();

                // Get all controllers for this step
                Splitter.on(",").omitEmptyStrings().split(configurationLine[0]).forEach(controller ->
                        controllers.add(resolveControllerByName(controller)));

                // Get the duration of the step
                long duration = Duration.parse("PT" + configurationLine[1]).toMillis();
                BenchmarkStep step = new BenchmarkStep(controllers, duration);

                logger.info("Added to test plan: {}", step.toString());
                plan.addStep(step);
            });

            logger.info("Parsed configuration file successfully, woohoo!");
            return plan;

        } catch (IOException e) {
            throw new RuntimeException("Could not read configuration file " + configurationFile + ", nothing to do.");
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Your configuration is invalid! Please refer to the README");
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Could not parse the duration of at least one step! this should be in ISO-8601 standard, without the 'PT' prefix", e);
        }
    }

    private static BaseController resolveControllerByName(String controllerName) {

        switch (controllerName.toUpperCase()) {
            case "INDEX":
                return new IndexingController();
            case "SEARCH":
                return new SearchController();
            case "OPTIMIZE":
                return new OptimizeController();
            case "NOOP":
                return new NoopController();
            default:
                throw new RuntimeException("Unknown test step " + controllerName);
        }
    }
}
