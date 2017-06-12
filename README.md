# elasticsearch-benchmark-tool
Complex stress test tool that benchmark indexing and searching in Elasticsearch

## TODO: more documentation

## Configuration Example
Can be found under ConfigurationExample.conf

## Run
```bash
 docker run --rm -it -e GRAPHITE_SERVER="your-graphite-server.com" \
                     -e GRAPHITE_PREFIX="Prefix.under.graphite.root" \
                     -e SERVICE_HOST="BENCHMARK_TEST_NAME" \
                     -v /your/configuration.conf:/config.conf \
                     logzio/elasticsearch-benchmark-tool
```

## Build
```bash
mvn clean package docker:build
```
