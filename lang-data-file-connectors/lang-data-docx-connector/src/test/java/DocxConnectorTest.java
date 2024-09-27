import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.PathFileConstants;
import ai.langframework.langdatacore.exceptions.LoaderException;
import ai.langframework.langdatadocx.DocxConnector;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocxConnectorTest {
  private static final String DOCX_URL = PathFileConstants.DOCX_FILE_URL;
  private DocxConnector loader;
  private static final String DOTX_URL = PathFileConstants.DOTX_FILE_URL;
  private URL url;

  List<Document> document;

  @BeforeAll
  void initializeclient() {
    loader = new DocxConnector();
  }

  @Test
  void testLoadDataFromFile() throws Exception {
    File testFile;
    // Use try-with-resources to ensure the InputStream is closed properly
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.docx")) {
      assertNotNull(inputStream, "Input stream must not be null");

      // Create a temporary file from the input stream
      Path tempFilePath = Files.createTempFile("dataset-", ".docx");
      Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
      testFile = tempFilePath.toFile();

      // Ensure to delete the temp file on exit
      testFile.deleteOnExit();

      document = loader.loadData(testFile);
    }

    assertNotNull(document, "Loaded data must not be null");
    assertTrue(
        document.get(0).getText().contains("MySQL Database Connector"),
        "Response should contain the expected content");
    assertEquals(testFile.getName(), document.get(0).getMetadata().get("FileName"));
    assertEquals(testFile.getAbsolutePath(), document.get(0).getMetadata().get("Source"));
    assertEquals(String.valueOf(testFile.length()), document.get(0).getMetadata().get("FileSize"));
  }

  @Test
  void testLoadDataFromUrl() throws MalformedURLException, LoaderException {
    url = new URL(DOCX_URL);
    document = loader.loadData(DOCX_URL);

    assertNotNull(document, "Loaded data must not be null");
    assertTrue(
        document.get(0).getText().contains("MySQL Database Connector"),
        "Response should contain the expected content");
    assertEquals(url.getFile(), document.get(0).getMetadata().get("FileName"));
    assertEquals(DOCX_URL, document.get(0).getMetadata().get("Source"));
    assertEquals(
        String.valueOf(url.getFile().length()), document.get(0).getMetadata().get("FileSize"));
  }

  @Test
  void testLoadDotxData() throws IOException, LoaderException {
    url = new URL(DOTX_URL);
    document = loader.loadData(DOTX_URL);

    assertNotNull(document, "Loaded data must not be null");
    assertTrue(
        document.get(0).getText().contains("MySQL Database Connector"),
        "Response should contain the expected content");
    assertEquals(url.getFile(), document.get(0).getMetadata().get("FileName"));
    assertEquals(DOTX_URL, document.get(0).getMetadata().get("Source"));
    assertEquals(
        String.valueOf(url.getFile().length()), document.get(0).getMetadata().get("FileSize"));
  }
}
