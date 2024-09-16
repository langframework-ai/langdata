//import ai.langframework.langdatacore.Config;
//import ai.langframework.langdatazapier.ZapierConnector;
//import ai.langframework.langdatazapier.ZapierConnectorClient;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//
//import java.sql.Connection;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//public class ZapierConnectorTest {
//    private String accessKey = Config.getApiKey("ACCESS_KEY");
//
//    ZapierConnector client;
//
//    ZapierConnectorClient connectorClient;
//
//    @BeforeAll
//    void initializeclient(){
//        client = new ZapierConnector();
//        client.setAccessToken(accessKey);
//        client.initializeClient();
//        connectorClient = client.getConnectorClient();
//    }
//    @Test
//    void getSourceClient() {
//        Connection sourceClient = client.getSourceClient();
//        assertNotNull(sourceClient);
//    }
//    @Test
//    void testClient() {
//        assertNotNull(client);
//    }
//
//    @Test
//    void getlistActions() throws Exception {
//        Map<String, String> actionMap = connectorClient.listActions();
//        assertNotNull(actionMap);
//        System.out.println(actionMap);
//        assertEquals(3, actionMap.size());
//
//        assertEquals("01HRWFENMG7WBMZW5W5TFXB0SF", actionMap.get("Slack: Send Direct Message"));
//        assertEquals("01HRWG23C1J7NRBXCCCZWZJ5E2", actionMap.get("send email"));
//        assertEquals("01HSK8XMNKE23VKRW4EQ8C5KKE",actionMap.get("create page"));
//    }
//
//    @Test
//    void executeAction() throws Exception {
//
//        String result = connectorClient.executeAction("create page","create page with name of TestingPage");
//        assertEquals("Executed Successfully", result);
//    }
//
//}
