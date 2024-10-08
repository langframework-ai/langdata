import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.langframework.langdatacore.Config;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatas3.S3Connector;
import ai.langframework.langdatas3.S3ConnectorClient;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class S3ConnectorTest {

  private String keyId = Config.getApiKey("AWS_ACCESS_KEY_ID");
  private String accessKey = Config.getApiKey("AWS_SECRET_ACCESS_KEY");
  S3Connector loader;
  S3ConnectorClient connectorClient;

  @BeforeAll
  void initialize() {
    Assumptions.assumeTrue(keyId != null && !keyId.isEmpty(), "AWS_ACCESS_KEY_ID not set");
    Assumptions.assumeTrue(
        accessKey != null && !accessKey.isEmpty(), "AWS_SECRET_ACCESS_KEY not set");
    loader = new S3Connector();
    loader.setAccessKey(keyId);
    loader.setSecretKey(accessKey);
    loader.initializeClient();
    connectorClient = loader.getConnectorClient();
  }

  @Test
  @Tag("requiresApiKey")
  void loadData() throws Exception {
    String bucketName = "shared-public-resources-free-ai";
    String objectName = "test.txt";
    List<Document> documents = connectorClient.readData(bucketName, objectName);

    assertNotNull(documents);
    assertTrue(
        documents.get(0).getText().contains("MySQL Database Connector"),
        "Response should contain the expected content");
  }

  @Test
  @Tag("requiresApiKey")
  void uploadFIle() throws Exception {
    File testFile;
    // Use try-with-resources to ensure the InputStream is closed properly
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.csv")) {
      assertNotNull(inputStream, "Input stream must not be null");

      // Create a temporary file from the input stream
      Path tempFilePath = Files.createTempFile("dataset-", ".csv");
      Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
      testFile = tempFilePath.toFile();

      // Ensure to delete the temp file on exit
      testFile.deleteOnExit();
    }
    connectorClient.uploadFile("shared-public-resources-free-ai", "testS3UploadFile.csv", testFile);
  }
}
