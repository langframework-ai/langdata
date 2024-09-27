import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.PathFileConstants;
import ai.langframework.langdatatext.TextConnector;
import java.io.File;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TextConnectorTest {

  private static final String TEXT_FILE_PATH = "test.txt";
  private static final String MD_FILE_PATH = "test.md";

  private static final String TEXT_URL = PathFileConstants.TEXT_FILE_URL;
  private TextConnector loader;
  private static final String MD_URL = PathFileConstants.MD_FILE_URL;
  private File testFile;
  private URL url;
  List<Document> document;

  @BeforeEach
  void setUp() throws Exception {
    loader = new TextConnector();
    URL resourceUrl = getClass().getResource("/" + TEXT_FILE_PATH);
    if (resourceUrl == null) {
      throw new IllegalStateException("file.txt not found in classpath");
    }
    testFile = new File(resourceUrl.toURI());
    url = new URL(TEXT_URL);
  }

  @Test
  void testLoadDataFile() {
    document = loader.loadData(testFile);

    assertNotNull(document, "Loaded data must not be null");
    assertTrue(
        document.get(0).getText().contains("MySQL Database Connector"),
        "Response should contain the expected content");
    assertEquals(testFile.getName(), document.get(0).getMetadata().get("FileName"));
    assertEquals(testFile.getAbsolutePath(), document.get(0).getMetadata().get("Source"));
    assertEquals(String.valueOf(testFile.length()), document.get(0).getMetadata().get("FileSize"));
  }

  @Test
  void testLoadDataUrl() {
    document = loader.loadData(TEXT_URL);

    assertNotNull(document, "Loaded data must not be null");
    assertTrue(
        document.get(0).getText().contains("MySQL Database Connector"),
        "Response should contain the expected content");
    assertEquals(url.getFile(), document.get(0).getMetadata().get("FileName"));
    assertEquals(TEXT_URL, document.get(0).getMetadata().get("Source"));
    assertEquals(
        String.valueOf(url.getFile().length()), document.get(0).getMetadata().get("FileSize"));
  }

  @Test
  void testLoadDataMdFile() throws Exception {
    URL resourceUrl = getClass().getResource("/" + MD_FILE_PATH);
    if (resourceUrl == null) {
      throw new IllegalStateException("file.txt not found in classpath");
    }
    testFile = new File(resourceUrl.toURI());

    document = loader.loadData(testFile);

    assertNotNull(document, "Loaded data must not be null");
    assertTrue(
        document.get(0).getText().contains("MySQL Database Connector"),
        "Response should contain the expected content");
    assertEquals(testFile.getName(), document.get(0).getMetadata().get("FileName"));
    assertEquals(testFile.getAbsolutePath(), document.get(0).getMetadata().get("Source"));
    assertEquals(String.valueOf(testFile.length()), document.get(0).getMetadata().get("FileSize"));
  }

  @Test
  void testLoadDataMdUrl() throws Exception {
    url = new URL(MD_URL);
    document = loader.loadData(MD_URL);

    assertNotNull(document, "Loaded data must not be null");
    assertTrue(
        document.get(0).getText().contains("MySQL Database Connector"),
        "Response should contain the expected content");
    assertEquals(url.getFile(), document.get(0).getMetadata().get("FileName"));
    assertEquals(MD_URL, document.get(0).getMetadata().get("Source"));
    // Assuming the loader can determine the size of the file from URL and store it
    assertNotNull(document.get(0).getMetadata().get("FileSize"), "FileSize metadata should be set");
    assertEquals(
        String.valueOf(url.getFile().length()), document.get(0).getMetadata().get("FileSize"));
  }
}
