package io.logz.benchmarks.elasticsearch;
import com.udojava.jmx.wrapper.JMXBeanWrapper;
import io.logz.benchmarks.elasticsearch.benchmark.BenchmarkPlan;
import io.logz.benchmarks.elasticsearch.configuration.ConfigurationParser;
import io.logz.benchmarks.elasticsearch.elasticsearch.ElasticsearchController;
import io.logz.benchmarks.elasticsearch.metrics.IndexingMbean;
import io.logz.benchmarks.elasticsearch.metrics.SearchMbean;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.IntrospectionException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Created by roiravhon on 9/19/16.
 */
public class ElasticsearchBenchmarkToolMain {

    private final static Logger logger = LoggerFactory.getLogger(ConfigurationParser.class);
    private static final String DEFAULT_INDEXING_THREADS = "5";
    private static final String DEFAULT_SEARCHING_THREADS = "5";

    public static void main(String[] args) {

        try {

            CommandLine cmd = parseCliArguments(args);

            String configFile = cmd.getOptionValue("test-config", null);
            String elasticsearchAddress = cmd.getOptionValue("elasticsearch-address", null);
            int indexingThreads = Integer.parseInt(cmd.getOptionValue("indexing-threads", DEFAULT_INDEXING_THREADS));
            int searchThreads = Integer.parseInt(cmd.getOptionValue("search-threads", DEFAULT_SEARCHING_THREADS));

            ElasticsearchController esController = new ElasticsearchController(elasticsearchAddress);
            esController.createIndex();

            registerMbeans();

            BenchmarkPlan plan = ConfigurationParser.parseConfiguration(configFile);
            plan.execute(indexingThreads, searchThreads, esController);
        }
        catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            logger.error("This is fatal, bailing out..");
            System.exit(1);
        }
    }

    private static CommandLine parseCliArguments(String[] args) {

        Options options = new Options();
        logger.info("Parsing CLI arguments");

        Option testConfig = new Option("t", "test-config", true, "The test configuration");
        testConfig.setRequired(true);
        options.addOption(testConfig);

        Option elasticsearchAddress = new Option("e", "elasticsearch-address", true, "Your Elasticsearch address, without port and protocol");
        elasticsearchAddress.setRequired(true);
        options.addOption(elasticsearchAddress);

        Option indexingThreadsOption = new Option("i", "indexing-threads", true, "How many parallel indexing threads should run");
        indexingThreadsOption.setRequired(false);
        indexingThreadsOption.setType(int.class);
        options.addOption(indexingThreadsOption);

        Option searchThreadsOption = new Option("s", "search-threads", true, "How many parallel search threads should run");
        searchThreadsOption.setRequired(false);
        searchThreadsOption.setType(int.class);
        options.addOption(searchThreadsOption);


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            return parser.parse(options, args);

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("elasticsearch-benchmark-tool", options);

            throw new RuntimeException();
        }
    }

    private static void registerMbeans() {

        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

            IndexingMbean indexingMbean = IndexingMbean.getInstance();
            SearchMbean searchMbean = SearchMbean.getInstance();

            JMXBeanWrapper indexingMbeanWrapper = new JMXBeanWrapper(indexingMbean);
            JMXBeanWrapper searchMbeanWrapper = new JMXBeanWrapper(searchMbean);

            mbs.registerMBean(indexingMbeanWrapper, new ObjectName("io.logz.benchmarks.elasticsearch:type=Indexing,name=Indexing Metrics"));
            mbs.registerMBean(searchMbeanWrapper, new ObjectName("io.logz.benchmarks.elasticsearch:type=Search,name=Search Metrics"));

        } catch (IntrospectionException | MalformedObjectNameException | NotCompliantMBeanException |
                InstanceAlreadyExistsException | MBeanRegistrationException e) {
            throw new RuntimeException("Could not initialize JMX metrics!", e);
        }
    }
}
