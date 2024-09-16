//
//
//import ai.langframework.langdatacore.*;
//import ai.langframework.langdatacore.exceptions.LoaderException;
//import ai.langframework.langdatamilvus.MilvusConnector;
//import ai.langframework.langdatamilvus.MilvusConnectorClient;
//import ai.langframework.langdatatext.TextConnector;
//import io.milvus.client.MilvusServiceClient;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class MilvusConnectorTest {
//
//    private static final String TEXT_URL = PathFileConstants.TEXT_FILE_URL;
//    private String clusterEndpoint = Config.getApiKey("MILVUS_CLUSTER_ENDPOINT");
//    private String token = Config.getApiKey("MILVUS_TOKEN");
//    private String openAiKey = Config.getApiKey("OPENAI_API_KEY");
//    private MilvusConnector client;
//    private MilvusConnectorClient connectorClient;
//
//    @BeforeAll
//    void initializeClient()
//    {
//        client = new MilvusConnector();
//        client.setEndpoint(clusterEndpoint);
//        client.setToken(token);
//        client.setEmbeddingClientType(VectorInterface.EmbeddingClientType.OPENAI);
//        client.setModelApiKey(openAiKey);
//        client.initializeClient();
//        connectorClient = client.getConnectorClient();
//    }
//    @Test
//    void getSourceClient() {
//        MilvusServiceClient sourceClient =  client.getSourceClient();
//        assertNotNull(sourceClient);
//    }
//
//    @Test
//    void testClient() {
//        assertNotNull(client);
//    }
//
//    @Test
//    void createCollection()
//    {
//        connectorClient.createCollection("test",1536);
//    }
//
//    @Test
//    void testAddDocumentsAndSearchSimilarity() throws LoaderException {
//        connectorClient.setCollectionName("test");
//        List<Document> document = new TextConnector().loadData(TEXT_URL);
//        connectorClient.addDocuments(document);
//
//        List<Document> result = connectorClient.searchSimilarity("Connectors");
//
//        assertNotNull(result, "Result should not be null");
//        assertTrue(result.get(0).getText().toLowerCase().contains("connectors"),"The result returned should contain connectors");
//    }
//
//    @Test
//    void testDescribeCollection()
//    {
//        String response = connectorClient.describeCollection("test");
//        assertNotNull(response, "Result should not be null");
//        assertTrue(response.contains("test"),"The result returned should contain connectors");
//
//    }
//
//    @Test
//    void dropCollection()
//    {
//        String result = connectorClient.dropCollection("test");
//
//        assertNotNull(result);
//        assertEquals("Successfully dropped collection",result);
//    }
//
//}
