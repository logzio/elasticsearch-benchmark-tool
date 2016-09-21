package io.logz.benchmarks.elasticsearch;
import com.udojava.jmx.wrapper.JMXBeanWrapper;
import io.logz.benchmarks.elasticsearch.benchmark.BenchmarkPlan;
import io.logz.benchmarks.elasticsearch.configuration.ConfigurationParser;
import io.logz.benchmarks.elasticsearch.configuration.InvalidConfigurationException;
import io.logz.benchmarks.elasticsearch.metrics.GeneralMbean;
import io.logz.benchmarks.elasticsearch.metrics.IndexingMbean;
import io.logz.benchmarks.elasticsearch.metrics.SearchMbean;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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

            registerMbeans();
            BenchmarkPlan plan = ConfigurationParser.parseConfiguration(configFile);

            try {
                plan.getEsController().createIndex();
                plan.execute();

            } finally {
                plan.getEsController().deleteIndex();
            }
        }
        catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            logger.error("This is fatal, bailing out..");
            System.exit(1);
        } catch (InvalidConfigurationException e) {
            logger.error("You configuration is invalid!");
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    private static CommandLine parseCliArguments(String[] args) {

        Options options = new Options();
        logger.info("Parsing CLI arguments");

        Option testConfig = new Option("t", "test-config", true, "The test configuration");
        testConfig.setRequired(true);
        options.addOption(testConfig);

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
            GeneralMbean generalMbean = GeneralMbean.getInstance();

            JMXBeanWrapper indexingMbeanWrapper = new JMXBeanWrapper(indexingMbean);
            JMXBeanWrapper searchMbeanWrapper = new JMXBeanWrapper(searchMbean);
            JMXBeanWrapper generalMbeanWrapper = new JMXBeanWrapper(generalMbean);

            mbs.registerMBean(indexingMbeanWrapper, new ObjectName("io.logz.benchmarks.elasticsearch:type=Indexing,name=Indexing Metrics"));
            mbs.registerMBean(searchMbeanWrapper, new ObjectName("io.logz.benchmarks.elasticsearch:type=Search,name=Search Metrics"));
            mbs.registerMBean(generalMbeanWrapper, new ObjectName("io.logz.benchmarks.elasticsearch:type=General,name=General Metrics"));

        } catch (IntrospectionException | MalformedObjectNameException | NotCompliantMBeanException |
                InstanceAlreadyExistsException | MBeanRegistrationException e) {
            throw new RuntimeException("Could not initialize JMX metrics!", e);
        }
    }
}
