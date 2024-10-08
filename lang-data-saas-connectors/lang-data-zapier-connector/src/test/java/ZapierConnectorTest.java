import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ai.langframework.langdatacore.Config;
import ai.langframework.langdatazapier.ZapierConnector;
import ai.langframework.langdatazapier.ZapierConnectorClient;
import java.sql.Connection;
import java.util.Map;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ZapierConnectorTest {
  private String accessKey = Config.getApiKey("ACCESS_KEY");

  ZapierConnector client;

  ZapierConnectorClient connectorClient;

  @BeforeAll
  void initializeclient() {
    Assumptions.assumeTrue(accessKey != null && !accessKey.isEmpty(), "Zapier key not set");
    client = new ZapierConnector();
    client.setAccessToken(accessKey);
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
  void getlistActions() throws Exception {
    Map<String, String> actionMap = connectorClient.listActions();
    assertNotNull(actionMap);
    System.out.println(actionMap);
    assertEquals(3, actionMap.size());

    assertEquals("01HRWFENMG7WBMZW5W5TFXB0SF", actionMap.get("Slack: Send Direct Message"));
    assertEquals("01HRWG23C1J7NRBXCCCZWZJ5E2", actionMap.get("send email"));
    assertEquals("01HSK8XMNKE23VKRW4EQ8C5KKE", actionMap.get("create page"));
  }

  @Test
  @Tag("requiresApiKey")
  void executeAction() throws Exception {

    String result =
        connectorClient.executeAction("create page", "create page with name of TestingPage");
    assertEquals("Executed Successfully", result);
  }
}
