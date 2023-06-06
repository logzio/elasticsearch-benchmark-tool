# THIS PROJECT IS NO LONGER MAINTAINED

# elasticsearch-benchmark-tool
Stress test tool that benchmark indexing and searching in Elasticsearch


## How it is working
The tool is getting a configuration file, which represent the test plan.  
Each step can have 1 or more of the following controllers:
 - Indexing - Index 1k bulks of log like documents (5 different options, with some fields getting different data based on common cardinality factor and always changing timestamp)
 - Search - Iterate over 5 different Kibana like searches
 - Optimize - Force merge the index
 - Noop - Do nothing  
 
The tool produce metrics as JMX counters, and the [Jmx2Graphite](https://github.com/logzio/jmx2graphite) tool is sending those to your graphite server.

## Configuration Example
Can be found under ConfigurationExample.conf

## Run
```bash
 docker run --rm -it -e GRAPHITE_SERVER="your-graphite-server.com" \
                     -e GRAPHITE_PREFIX="Prefix.under.graphite.root" \
                     -e SERVICE_HOST="BENCHMARK_TEST_NAME" \
                     -v /your/configuration.conf:/config.conf \
                     -v /your/templates:/templates \
                     logzio/elasticsearch-benchmark-tool
```

## Build
```bash
mvn clean package docker:build
```
