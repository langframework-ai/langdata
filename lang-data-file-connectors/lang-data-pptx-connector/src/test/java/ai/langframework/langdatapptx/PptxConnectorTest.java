package ai.langframework.langdatapptx;

import static org.junit.jupiter.api.Assertions.*;

import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.PathFileConstants;
import ai.langframework.langdatacore.exceptions.LoaderException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PptxConnectorTest {

  private static final String PPTX_URL = PathFileConstants.PPTX_FILE_URL;
  private PptxConnector loader;
  private URL url;

  List<Document> document;

  @BeforeAll
  void initializeclient() {
    loader = new PptxConnector();
  }

  @Test
  void testLoadDataFromFile() throws Exception {
    File testFile;

    // Use try-with-resources to ensure the InputStream is closed properly
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.pptx")) {
      assertNotNull(inputStream, "Input stream must not be null");

      // Create a temporary file from the input stream
      Path tempFilePath = Files.createTempFile("dataset-", ".pptx");
      Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
      testFile = tempFilePath.toFile();

      // Ensure to delete the temp file on exit
      testFile.deleteOnExit();

      document = loader.loadData(testFile);
    }

    assertNotNull(document, "Loaded data must not be null");
    assertTrue(
        document.get(0).getText().toLowerCase().contains("lang framework"),
        "Response should contain the expected content");
    assertEquals(testFile.getName(), document.get(0).getMetadata().get("FileName"));
    assertEquals(testFile.getAbsolutePath(), document.get(0).getMetadata().get("Source"));
    assertEquals(String.valueOf(testFile.length()), document.get(0).getMetadata().get("FileSize"));
  }

  @Test
  void testLoadData() throws IOException, LoaderException {
    url = new URL(PPTX_URL);
    document = loader.loadData(PPTX_URL);

    assertNotNull(document, "Loaded data must not be null");
    assertTrue(
        document.get(0).getText().toLowerCase().contains("lang framework"),
        "Response should contain the expected content");
    assertEquals(url.getFile(), document.get(0).getMetadata().get("FileName"));
    assertEquals(PPTX_URL, document.get(0).getMetadata().get("Source"));
    // Assuming the loader can determine the size of the file from URL and store it
    assertNotNull(document.get(0).getMetadata().get("FileSize"), "FileSize metadata should be set");
    assertEquals(
        String.valueOf(url.getFile().length()), document.get(0).getMetadata().get("FileSize"));
  }
}
