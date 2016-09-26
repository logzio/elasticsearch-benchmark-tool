package io.logz.benchmarks.elasticsearch.elasticsearch;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import io.logz.benchmarks.elasticsearch.configuration.ElasticsearchConfiguration;
import io.logz.benchmarks.elasticsearch.exceptions.CouldNotCompleteBulkOperationException;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.mapping.PutMapping;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.mapper.core.DateFieldMapper;
import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Created by roiravhon on 9/19/16.
 */
public class ElasticsearchController {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchController.class);
    private static final int INDEX_LENGTH = 8;
    private static final int FIELD_CARDINALITY = 100;
    private static final int MAX_CHARS_IN_FIELD = 10;
    private static final int MIN_CHARS_IN_FIELD = 5;
    private static final String TEMPLATE_DOCUMENTS_RESOURCE_FOLDER = "templates/documents";
    private static final String TEMPLATE_SEARCHES_RESOURCE_FOLDER = "templates/searches";
    private static final String TIMESTAMP_PLACEHOLDER = "TIMESTAMP";
    private static final String RANDOM_STR_PLACEHOLDER = "RANDSTR";
    private static final String RANDOM_INT_PLACEHOLDER = "RANDINT";
    private static final String DEFAULT_TYPE = "benchmark";

    private final ElasticsearchConfiguration esConfig;
    private final ArrayList<String> randomFieldsList;
    private final ArrayList<String> rawDocumentsList;
    private final JestClient client;
    private final String indexName;

    private boolean indexCreated = false;
    private int lastSelectedDocument = 0;

    public ElasticsearchController(ElasticsearchConfiguration esConfig) {
        this.esConfig = esConfig;
        indexName = getRandomString(INDEX_LENGTH);

        logger.info("Random test index name set to: {}", indexName);

        client = initializeJestClient(esConfig);
        randomFieldsList = generateRandomFieldList();
        rawDocumentsList = getResourceDirectoryContent(TEMPLATE_DOCUMENTS_RESOURCE_FOLDER);
    }

    public void createIndex() {
        Settings.Builder settingsBuilder = Settings.settingsBuilder();
        settingsBuilder.put("number_of_shards", esConfig.getNumberOfShards());
        settingsBuilder.put("number_of_replicas", esConfig.getNumberOfReplicas());

        // No nice way to build this with elasticsearch library
        String mappings = "{ \"" + DEFAULT_TYPE + "\" : { \"properties\" : { \"@timestamp\" : {\"type\" : \"date\", \"format\" : \"epoch_millis\"}}}}";

        try {
            logger.info("Creating test index on ES, named {} with {} shards and {} replicas", indexName, esConfig.getNumberOfShards(), esConfig.getNumberOfReplicas());
            client.execute(new CreateIndex.Builder(indexName).settings(settingsBuilder.build().getAsMap()).build());
            client.execute(new PutMapping.Builder(indexName, DEFAULT_TYPE, mappings).build());

            indexCreated = true;

        } catch (IOException e) {
            throw new RuntimeException("Could not create index in elasticsearch!", e);
        }
    }

    public void deleteIndex() {
        try {
            if (indexCreated) {
                logger.info("Deleting test index {} from ES", indexName);
                client.execute(new DeleteIndex.Builder(indexName).build());
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not delete index from ES!");
        }
    }

    public ArrayList<String> getMultipleDocuments(int numberOfDocument) {

        ArrayList<String> tempList = new ArrayList<>(numberOfDocument + 1);
        IntStream.range(0, numberOfDocument).forEach((i) -> tempList.add(getDocument()));
        return tempList;
    }

    public String getDefaultType() {
        return DEFAULT_TYPE;
    }

    public String getIndexName() {
        return indexName;
    }

    // Return the number of failed documents
    public int executeBulk(Bulk bulk) throws CouldNotCompleteBulkOperationException {
        try {

            BulkResult result = client.execute(bulk);
            return result.getFailedItems().size();

        } catch (IOException e) {
            throw new CouldNotCompleteBulkOperationException();
        }
    }

    private String getDocument() {

        String currDocument = rawDocumentsList.get(lastSelectedDocument % rawDocumentsList.size());
        currDocument = currDocument.replace(TIMESTAMP_PLACEHOLDER, String.valueOf(System.currentTimeMillis()));

        // Replacing all marks with values
        while (true) {

            currDocument = currDocument.replaceFirst(RANDOM_STR_PLACEHOLDER, getRandomField());
            currDocument = currDocument.replaceFirst(RANDOM_INT_PLACEHOLDER, String.valueOf(getRandomFieldInt()));

            // Breaking out if we are done replacing
            if (!currDocument.contains(RANDOM_STR_PLACEHOLDER) && !currDocument.contains(RANDOM_INT_PLACEHOLDER))
                break;
        }

        lastSelectedDocument++;
        return currDocument;
    }

    private String getRandomField() {
        int index = ThreadLocalRandom.current().nextInt(0, FIELD_CARDINALITY);
        return randomFieldsList.get(index);
    }

    private int getRandomFieldInt() {
        return ThreadLocalRandom.current().nextInt(0, 10000 + 1);
    }

    private String getRandomString(int length) {
        try {
            return UUID.randomUUID().toString().substring(0, length);
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Cannot create random string of size " + length + "! this is probably due to internal parameters changes you made.", e);
        }
    }

    private String getRandomStringInRandomSize(int minSize, int maxSize) {
        int length = ThreadLocalRandom.current().nextInt(minSize, maxSize + 1);
        return getRandomString(length);
    }

    private JestClient initializeJestClient(ElasticsearchConfiguration esConfig) {
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder(esConfig.getElasticsearchProtocol() + "://" + esConfig.getElasticsearchAddress() + ":" + esConfig.getElasticsearchPort())
                .multiThreaded(true)
                .build());

        logger.info("Creating Jest client to handle all ES operations");
        return factory.getObject();
    }

    private ArrayList<String> generateRandomFieldList() {
        ArrayList<String> tempRandomFieldList = new ArrayList<>(FIELD_CARDINALITY);

        IntStream.range(0, FIELD_CARDINALITY).forEach((i) ->
                tempRandomFieldList.add(getRandomStringInRandomSize(MIN_CHARS_IN_FIELD, MAX_CHARS_IN_FIELD)));

        return tempRandomFieldList;
    }

    private ArrayList<String> getResourceDirectoryContent(String directoryName) {
        try {
            ArrayList<String> tempFilesContentList = new ArrayList<>();

            URL url = Resources.getResource(directoryName + "/");

            File directory = new File(url.toURI());

            if (!directory.isDirectory())
                throw new RuntimeException(directoryName + " is not a directory! cant read files in it");

            for (File currFile : directory.listFiles()) {
                tempFilesContentList.add(Files.toString(currFile, Charset.defaultCharset())
                                        .replace("\n", "").replace("\r", ""));
            }

            return tempFilesContentList;

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Could not read files in resources directory " + directoryName, e);
        }
    }
}
