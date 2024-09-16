//import ai.langframework.langdataopenai.OpenAIConnector;
//import ai.langframework.langdataopenai.OpenAIConnectorClient;
//import ai.langframework.langdatacore.Config;
//import ai.langframework.langdatacore.Document;
//import ai.langframework.langdatacore.PathFileConstants;
//import ai.langframework.langdatacore.exceptions.LoaderException;
//import ai.langframework.langdatatext.TextConnector;
//import com.theokanning.openai.service.OpenAiService;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class OpenAIConnectorTest {
//
//    private String openAiApiKey = Config.getApiKey("OPENAI_API_KEY");
//
//    private OpenAIConnector client;
//    private OpenAIConnectorClient connectorClient;
//    private static final String TEXT_URL = PathFileConstants.TEXT_FILE_URL;
//
//    @BeforeAll
//    void initializeclient(){
//        client = new OpenAIConnector();
//        client.setApiKey(openAiApiKey);
//        client.initializeClient();
//        connectorClient = client.getConnectorClient();
//    }
//
//    @Test
//    void getSourceClient() {
//        OpenAiService sourceClient = client.getSourceClient();
//        assertNotNull(sourceClient);
//    }
//
//    @Test
//    void testClient() {
//        assertNotNull(client);
//    }
//
//    @Test
//    void testSimpleChat(){
//        String response = connectorClient.simpleChat("Tell me a joke. The word 'joke' should be in the response.", "gpt-3.5-turbo-instruct");
//        assertNotNull(response, "Response should not be null");
//        assertTrue(response.toLowerCase().contains("joke"), "The response should contain the word 'joke'");
//    }
//
//    @Test
//    void testChatMessage(){
//        Map<OpenAIConnectorClient.MessageType, String > messages = new HashMap<>();
//        messages.put(OpenAIConnectorClient.MessageType.USER, "Tell me a joke. The word 'joke' should be in the response.");
//
//        String response = connectorClient.chatMessage(messages, "gpt-3.5-turbo", 0.8);
//        assertNotNull(response, "Response should not be null");
//        assertTrue(response.toLowerCase().contains("joke"), "The response should contain the word 'joke'");
//    }
//
//    @Test
//    void testEmbedding(){
//        List<Float[]> response = connectorClient.embedding(List.of("Tell me a joke"), "text-embedding-3-small");
//
//        assertNotNull(response, "Embeddings should not be null");
//        assertTrue(response.size() > 0, "Embeddings array should not be empty");
//    }
//
//    @Test
//    void testCallWithContext() throws LoaderException {
//        List<Document> document = new TextConnector().loadData(TEXT_URL);
//        String response = connectorClient.callWithContext("Summarize", document);
//
//        assertNotNull(response, "Embeddings should not be null");
//        assertTrue(response.length() > 0, "Response should not be empty");
//        assertTrue(response.contains("connectors"), "The summary should contains information about the connectors");
//    }
//}
