package io.logz.benchmarks.elasticsearch.elasticsearch;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import io.logz.benchmarks.elasticsearch.configuration.ElasticsearchConfiguration;
import io.logz.benchmarks.elasticsearch.exceptions.CouldNotCompleteBulkOperationException;
import io.logz.benchmarks.elasticsearch.exceptions.CouldNotExecuteSearchException;
import io.logz.benchmarks.elasticsearch.exceptions.CouldNotOptimizeException;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Bulk;
import io.searchbox.core.BulkResult;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.ForceMerge;
import io.searchbox.indices.mapping.PutMapping;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.settings.Settings;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
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
    private static final int JEST_READ_TIMEOUT = 2 * 60 * 1000;
    private static final String TEMPLATE_DOCUMENTS_RESOURCE_PATTERN = ".*templates/documents.*";
    private static final String TEMPLATE_SEARCHES_RESOURCE_PATTERN = ".*templates/searches.*";
    private static final String TIMESTAMP_PLACEHOLDER = "TIMESTAMP";
    private static final String RANDOM_STR_PLACEHOLDER = "RANDSTR";
    private static final String RANDOM_INT_PLACEHOLDER = "RANDINT";
    private static final String DEFAULT_TYPE = "benchmark";

    private final ElasticsearchConfiguration esConfig;
    private final ArrayList<String> randomFieldsList;
    private final ArrayList<String> rawDocumentsList;
    private final ArrayList<String> searchesList;
    private final JestClient client;
    private final String indexName;
    private final Optional<String> indexPrefix;
    private final AtomicInteger lastSelectedDocument;
    private final AtomicInteger lastSelectedSearch;

    private boolean indexCreated = false;

    public ElasticsearchController(ElasticsearchConfiguration esConfig) {
        this.esConfig = esConfig;
        lastSelectedDocument = new AtomicInteger(0);
        lastSelectedSearch = new AtomicInteger(0);

        indexPrefix = Optional.ofNullable(esConfig.getIndexPrefix());

        indexName = indexPrefix.orElse("") + getRandomString(INDEX_LENGTH);

        logger.info("Random test index name set to: {}", indexName);

        client = initializeJestClient(esConfig);
        randomFieldsList = generateRandomFieldList();
        rawDocumentsList = loadDocuments(esConfig.getDocumentsPath(), TEMPLATE_DOCUMENTS_RESOURCE_PATTERN);
        searchesList = loadDocuments(esConfig.getSearchesPath(), TEMPLATE_SEARCHES_RESOURCE_PATTERN);
    }

    private ArrayList<String> loadDocuments(String path, String resource) {
        if (path != null) {
            try {
                return getDirectoryContent(path);
            } catch (RuntimeException e) {
                logger.error("failed to load documents from path " + path + ". Fallback, going to load default documents");
            }
        }

        return getResourceDirectoryContent(resource);
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
            throw new RuntimeException("Could not delete index from ES!", e);
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

    public Optional<String> getIndexPrefix() {
        return indexPrefix;
    }

    // Return the number of failed documents
    public int executeBulk(Bulk bulk) throws CouldNotCompleteBulkOperationException {
        try {

            BulkResult result = client.execute(bulk);
            return result.getFailedItems().size();

        } catch (IOException e) {
            throw new CouldNotCompleteBulkOperationException(e);
        }
    }

    // Returns the number of documents found
    public int executeSearch(Search search) throws CouldNotExecuteSearchException {

        try {
            SearchResult result = client.execute(search);

            if (result.isSucceeded()) {
                return result.getTotal();
            } else {
                logger.debug(result.getErrorMessage());
                throw new CouldNotExecuteSearchException();
            }
        } catch (IOException e) {
            throw new CouldNotExecuteSearchException(e);
        }
    }

    public void executeForceMerge(ForceMerge forceMerge) throws CouldNotOptimizeException {
        try {
            JestResult result = client.execute(forceMerge);
            if (!result.isSucceeded())
                throw new CouldNotOptimizeException(result.getErrorMessage());

        } catch (IOException e) {
            throw new CouldNotOptimizeException(e);
        }
    }

    public String getSearch() {
        String currSearch = searchesList.get(lastSelectedSearch.get() % searchesList.size());
        lastSelectedSearch.incrementAndGet();
        return currSearch;
    }

    private String getDocument() {

        String currDocument = rawDocumentsList.get(lastSelectedDocument.get() % rawDocumentsList.size());
        currDocument = currDocument.replace(TIMESTAMP_PLACEHOLDER, String.valueOf(System.currentTimeMillis()));

        // Replacing all marks with values
        while (true) {

            currDocument = currDocument.replaceFirst(RANDOM_STR_PLACEHOLDER, getRandomField());
            currDocument = currDocument.replaceFirst(RANDOM_INT_PLACEHOLDER, String.valueOf(getRandomFieldInt()));

            // Breaking out if we are done replacing
            if (!currDocument.contains(RANDOM_STR_PLACEHOLDER) && !currDocument.contains(RANDOM_INT_PLACEHOLDER))
                break;
        }

        lastSelectedDocument.incrementAndGet();
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
        HttpClientConfig.Builder httpClientBuilder = new HttpClientConfig
                .Builder(esConfig.getElasticsearchProtocol() + "://" + esConfig.getElasticsearchAddress() + ":" + esConfig.getElasticsearchPort())
                .multiThreaded(true)
                .readTimeout(JEST_READ_TIMEOUT);

        if (StringUtils.isNotEmpty(esConfig.getUserName()) && StringUtils.isNotEmpty(esConfig.getPassword())) {
            httpClientBuilder.defaultCredentials(esConfig.getUserName(), esConfig.getPassword());
        }

        factory.setHttpClientConfig(httpClientBuilder.build());

        logger.info("Creating Jest client to handle all ES operations");
        return factory.getObject();
    }

    private ArrayList<String> generateRandomFieldList() {
        ArrayList<String> tempRandomFieldList = new ArrayList<>(FIELD_CARDINALITY);

        IntStream.range(0, FIELD_CARDINALITY).forEach((i) ->
                tempRandomFieldList.add(getRandomStringInRandomSize(MIN_CHARS_IN_FIELD, MAX_CHARS_IN_FIELD)));

        return tempRandomFieldList;
    }

    private ArrayList<String> getResourceDirectoryContent(String resourcePattern) {
        ArrayList<String> tempFilesContentList = new ArrayList<>();

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                                                        .setUrls(ClasspathHelper.forPackage("io.logz"))
                                                        .setScanners(new ResourcesScanner())
                                                        .filterInputsBy(new FilterBuilder().include(resourcePattern)));
        Set<String> properties = reflections.getResources(Pattern.compile(".*\\.json"));

        properties.forEach((resourceName) -> {

            URL resourceUrl = Resources.getResource(resourceName);
            try {
                tempFilesContentList.add(Resources.toString(resourceUrl, Charset.forName("utf-8")).replace("\n", ""));

            } catch (IOException e) {
                logger.info("Could not read file {}", resourceUrl.toString());
            }
        });

        if (tempFilesContentList.isEmpty())
            throw new RuntimeException("Did not find any files under "+ resourcePattern +"!");

        return tempFilesContentList;
    }

    private ArrayList<String> getDirectoryContent(String path) {
        ArrayList<String> tempFilesContentList = new ArrayList<>();

        File directory = new File(path);
        if (!directory.isDirectory()) {
            throw new RuntimeException("Did not find any files under "+ path +"!");
        }

        File[] documents = directory.listFiles();

        if (documents == null || documents.length == 0) {
            throw new RuntimeException("Did not find any files under "+ path +"!");
        }

        for (File doc : documents) {
            try {
                String join = String.join("", Files.readLines(doc, StandardCharsets.UTF_8));
                logger.info(join);
                tempFilesContentList.add(join);
            } catch (IOException e) {
                logger.info("Could not read file {}", doc.getAbsolutePath());
            }
        }
        return tempFilesContentList;
    }
}
