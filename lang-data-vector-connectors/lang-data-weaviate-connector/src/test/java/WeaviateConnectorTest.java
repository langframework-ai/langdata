import ai.langframework.langdatacore.*;
import ai.langframework.langdatacore.exceptions.LoaderException;
import ai.langframework.langdatatext.TextConnector;
import ai.langframework.langdataweaviate.WeaviateConnector;
import ai.langframework.langdataweaviate.WeaviateConnectorClient;
import io.weaviate.client.WeaviateClient;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WeaviateConnectorTest {
    private String weaviateApiUrl = Config.getApiKey("WEAVIATE_API_URL");
    private String weaviateApiKey = Config.getApiKey("WEAVIATE_API_KEY");
    private WeaviateConnector client;
    private WeaviateConnectorClient connectorClient;
    private VectorInterface.EmbeddingClientType embeddingClientType = VectorInterface.EmbeddingClientType.OPENAI;
    private String openAiKey = Config.getApiKey("OPENAI_API_KEY");
    private static final String TEXT_URL = PathFileConstants.TEXT_FILE_URL;


    @BeforeAll
    void initializeclient(){
        Assumptions.assumeTrue(openAiKey != null && !openAiKey.isEmpty(), "Open AI API key not set");
        Assumptions.assumeTrue(weaviateApiUrl != null && !weaviateApiUrl.isEmpty(), "Weaviate API Url not set");
        Assumptions.assumeTrue(weaviateApiKey != null && !weaviateApiKey.isEmpty(), "Weaviate API Key not set");
        client = new WeaviateConnector();
        client.setApiKey(weaviateApiKey);
        client.setUrl(weaviateApiUrl);
        client.setEmbeddingClientType(embeddingClientType);
        client.setModelApiKey(openAiKey);
        client.initializeClient();
        connectorClient = client.getConnectorClient();
    }

    @Test
    @Order(1)
    @Tag("requiresApiKey")
    void getSourceClient() {
        WeaviateClient sourceClient = client.getSourceClient();
        assertNotNull(sourceClient);
    }

    @Test
    @Order(2)
    @Tag("requiresApiKey")
    void testClient() {
        assertNotNull(client);
    }
    @Test
    @Order(3)
    @Tag("requiresApiKey")
    void createClass() {
        connectorClient.createClass("Dummy", "The class contains dummy data");
    }

    @Test
    @Order(4)
    @Tag("requiresApiKey")
    void getSchema() {
        String schema = connectorClient.getSchema();
        assertNotNull(schema);
        Logger.info(schema);
    }

    @Test
    @Order(5)
    @Tag("requiresApiKey")
    void testGetClass() {
        String result = connectorClient.getClass("Dummy");

        assertNotNull(result, "Result should not be null");
        assertTrue(result.contains("Dummy"),"The class returned should be Dummy");
    }

    @Test
    @Order(6)
    @Tag("requiresApiKey")
    void testAddDocumentsAndSearchSimilarity() throws LoaderException {
        connectorClient.setClassName("Dummy");
        List<Document> document = new TextConnector().loadData(TEXT_URL);
        connectorClient.addDocuments(document);

        List<Document> result = connectorClient.searchSimilarity("Connectors");

        assertNotNull(result, "Result should not be null");
        assertTrue(result.get(0).getText().toLowerCase().contains("connectors"),"The result returned should contain connectors");
    }

    @Test
    @Order(7)
    @Tag("requiresApiKey")
    void testDeleteClass() {
        connectorClient.deleteClass("Dummy");
    }

}