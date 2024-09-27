import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.langframework.langdatacore.*;
import ai.langframework.langdatacore.exceptions.LoaderException;
import ai.langframework.langdatapinecone.PineconeConnector;
import ai.langframework.langdatapinecone.PineconeConnectorClient;
import ai.langframework.langdatatext.TextConnector;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PineconeConnectorTest {
  private String pineconeApiKey = Config.getApiKey("PINECONE_API_KEY");
  private String environment = Config.getApiKey("PINECONE_ENVIRONMENT");
  private String indexName = Config.getApiKey("PINECONE_INDEX");
  private String connectionUrl = Config.getApiKey("PINECONE_CONNECTION_URL");
  private String projectName = Config.getApiKey("PINECONE_PROJECT_NAME");
  private String modelApiKey = Config.getApiKey("OPENAI_API_KEY");
  private VectorInterface.EmbeddingClientType embeddingClientType =
      VectorInterface.EmbeddingClientType.OPENAI;
  private PineconeConnector client;
  private PineconeConnectorClient connectorClient;
  private static final String TEXT_URL = PathFileConstants.TEXT_FILE_URL;

  @BeforeAll
  void initializeClient() {
    Assumptions.assumeTrue(
        pineconeApiKey != null && !pineconeApiKey.isEmpty(), "PineconeAPI key not set");
    Assumptions.assumeTrue(
        connectionUrl != null && !connectionUrl.isEmpty(), "PineconeAPI url not set");
    Assumptions.assumeTrue(modelApiKey != null && !modelApiKey.isEmpty(), "OpenAPI key not set");
    client = new PineconeConnector();
    client.setApiKey(pineconeApiKey);
    client.setEnvironment(environment);
    client.setProjectName(projectName);
    client.setIndex(indexName);
    client.setConnectionUrl(connectionUrl);
    client.setEmbeddingClientType(embeddingClientType);
    client.setModelApiKey(modelApiKey);
    client.initializeClient();

    connectorClient = client.getConnectorClient();
  }

  @Test
  @Order(1)
  @Tag("requiresApiKey")
  void getSourceClient() {
    assertNotNull(client.getSourceClient());
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
  void testAddDocumentsAndSearchSimilarity() throws LoaderException, IOException {
    connectorClient.setNamespace("test");
    connectorClient.setTopK(3);

    List<Document> document = new TextConnector().loadData(TEXT_URL);
    connectorClient.addDocuments(document);

    List<Document> result = connectorClient.searchSimilarity("Connectors");

    assertNotNull(result, "Result should not be null");
    assertTrue(
        result.get(0).getText().toLowerCase().contains("connectors"),
        "The result returned should contain connectors");
  }
}
