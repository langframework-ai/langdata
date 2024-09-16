//import ai.langframework.langdatacohereai.CohereAIConnector;
//import ai.langframework.langdatacohereai.CohereAIConnectorClient;
//import ai.langframework.langdatacore.Config;
//import ai.langframework.langdatacore.Document;
//import ai.langframework.langdatacore.PathFileConstants;
//import ai.langframework.langdatacore.exceptions.LoaderException;
//import ai.langframework.langdatatext.TextConnector;
//import com.cohere.api.Cohere;
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
//class CohereAIConnectorTest {
//    private String cohereApiKey = Config.getApiKey("COHERE_API_KEY");;
//    private CohereAIConnector client ;
//    private CohereAIConnectorClient connectorClient;
//    private static final String TEXT_URL = PathFileConstants.TEXT_FILE_URL;
//
//    @BeforeAll
//    void initializeclient(){
//        client = new CohereAIConnector();
//        client.setApiKey(cohereApiKey);
//        client.initializeClient();
//        connectorClient = client.getConnectorClient();
//    }
//
//    @Test
//    void getSourceClient() {
//        Cohere sourceClient = client.getSourceClient();
//        assertNotNull(sourceClient);
//    }
//
//    @Test
//    void testClient() {
//        assertNotNull(client);
//    }
//
//    @Test
//    void testChat(){
//        String response = connectorClient.simpleChat("Tell me a joke");
//        assertNotNull(response, "Response should not be null");
//        assertTrue(response.toLowerCase().contains("joke"), "The response should contain a joke");
//    }
//
//    @Test
//    void testGenerate(){
//        String response = connectorClient.simpleGenerate("Tell me a joke");
//        System.out.println(response);
//        assertNotNull(response, "Response should not be null");
//        assertTrue(response.toLowerCase().contains("joke"), "The response should contain a joke");
//
//    }
//
//    @Test
//    void testClassify(){
//        Map<String, String> movieReviews = new HashMap<>();
//        movieReviews.put("The cinematography and acting were outstanding; a must-watch film!", "Positive");
//        movieReviews.put("Unfortunately, the plot was confusing and the characters were underdeveloped.", "Negative");
//        movieReviews.put("I was on the edge of my seat throughout the entire movie. Thrilling!", "Positive");
//        movieReviews.put("The dialogue felt forced, and the pacing was too slow. Disappointing.", "Negative");
//        movieReviews.put("A heartwarming and touching story. I highly recommend it.", "Positive");
//        movieReviews.put("The special effects were impressive, but the story lacked depth. Mediocre.", "Negative");
//        movieReviews.put("An absolute masterpiece! The performances were exceptional.", "Positive");
//        movieReviews.put("I expected more from this film. It fell short of my expectations.", "Negative");
//
//        String response = connectorClient.classify(movieReviews, "A visually stunning film with a captivating storyline. I was thoroughly impressed!");
//
//        assertNotNull(response, "Response should not be null");
//        assertTrue(response.toLowerCase().contains("positive"), "Response should contain the expected content");
//    }
//
//    @Test
//    void testCovertTextToEmbeddings(){
//        Float[] vectors = connectorClient.embedText("Tell me a joke");
//
//        assertNotNull(vectors, "Embeddings should not be null");
//        assertTrue(vectors.length > 0, "Embeddings array should not be empty");
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