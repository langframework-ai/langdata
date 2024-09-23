
import ai.langframework.langdatapgvector.PgvectorConnector;
import ai.langframework.langdatapgvector.PgvectorConnectorClient;
import ai.langframework.langdatacore.*;
import ai.langframework.langdatacore.exceptions.LoaderException;
import ai.langframework.langdatatext.TextConnector;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PgvectorConnectorTest {
     private String driver = Config.getApiKey("SQL_DRIVER");

     private String url = Config.getApiKey("SQL_URL");
     private String username = Config.getApiKey("SQL_USERNAMEDB");
     private String password = Config.getApiKey("SQL_PASSWORD");
     private String openAiKey = Config.getApiKey("OPENAI_API_KEY");
     private PgvectorConnector client;
     private PgvectorConnectorClient connectorClient;
     private static final String TEXT_URL = PathFileConstants.TEXT_FILE_URL;

    @BeforeAll
    void initializeClient() throws SQLException {
        Assumptions.assumeTrue(openAiKey != null && !openAiKey.isEmpty(), "Open AI API key not set");
        Assumptions.assumeTrue(username != null && !username.isEmpty(), "Pgvector username not set");
        Assumptions.assumeTrue(password != null && !password.isEmpty(), "Pgvector password not set");
        client = new PgvectorConnector();
        client.setDriver(driver);
        client.setUrl(url);
        client.setUsername(username);
        client.setPassword(password);
        client.setEmbeddingClientType(VectorInterface.EmbeddingClientType.OPENAI);
        client.setModelApiKey(openAiKey);
        client.initializeClient();
        connectorClient = client.getConnectorClient();
    }

    @Test
    @Tag("requiresApiKey")
    void getSourceClient() {
        Connection sourceClient = client.getSourceClient();
        assertNotNull(sourceClient);
    }

    @Test
    @Tag("requiresApiKey")
    void testClient() {
        assertNotNull(client);
    }

    @Test
    @Tag("requiresApiKey")
    void testCreateTable() throws SQLException {
        //make table
        Map<String, String> columns = new HashMap<>();
        columns.put("id", "bigserial");
        columns.put("vector", "vector(1536)");
        columns.put("text", "text");
        // Add more columns as needed
        String result = connectorClient.createTable("test", columns);
        assertNotNull(result);
        assertEquals("Created Successfully",result);
    }

    @Test
    @Tag("requiresApiKey")
    void testAddDocumentsAndSearchSimilarity() throws LoaderException {
        connectorClient.setTableName("test");
        List<Document> document = new TextConnector().loadData(TEXT_URL);
        connectorClient.addDocuments(document);

        List<Document> result = connectorClient.searchSimilarity("Connectors");
        System.out.println(result.get(0).getText());
        assertNotNull(result, "Result should not be null");
        assertTrue(result.get(0).getText().toLowerCase().contains("connectors"),"The result returned should contain connectors");
    }

    @Test
    @Tag("requiresApiKey")
    void testDropTable() throws SQLException {
        String response = connectorClient.deleteTable("test");

        assertNotNull(response, "Result should not be null");
        assertTrue(response.contains("Table Dropped Successfully"));
    }
}
