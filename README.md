# elasticsearch-benchmark-tool
Complex stress test tool that benchmark indexing and searching in Elasticsearch

## Configuration Example
Can be found under ConfigurationExample.conf
### TODO: add description


## Run
```bash
 docker run --rm -it -e GRAPHITE_SERVER="graphite.staging.us-east-1.internal.logz.io" \
                     -e GRAPHITE_PREFIX="RoiBenchmark" \
                     -e SERVICE_HOST="ES_LOGZ_DEV_10" \
                     -v /your/configuration.conf:/config.conf \
                     registry.internal.logz.io:5000/elasticsearch-benchmark-tool
```

## Build
```bash
mvn clean package docker:build
```
