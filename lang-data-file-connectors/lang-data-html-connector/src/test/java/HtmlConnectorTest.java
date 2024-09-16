import ai.langframework.langdatahtml.HtmlConnector;
import ai.langframework.langdatacore.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static ai.langframework.langdatacore.PathFileConstants.HTML_FILE_URL;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HtmlConnectorTest {

    HtmlConnector loader;
    List<Document> document;

    @BeforeAll
    void initializeclient(){
        loader = new HtmlConnector();
    }
    @Test
    void testLoadDataFile() throws Exception {
        File testFile;

        // Use try-with-resources to ensure the InputStream is closed properly
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.html")) {
            assertNotNull(inputStream, "Input stream must not be null");

            // Create a temporary file from the input stream
            Path tempFilePath = Files.createTempFile("test-", ".html");
            Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            testFile = tempFilePath.toFile();

            // Ensure to delete the temp file on exit
            testFile.deleteOnExit();

            // Load data using the file
            document = loader.loadData(testFile);
        }

        assertNotNull(document, "Loaded data must not be null");
        assertTrue(document.get(0).getText().contains("HTML content"), "Response should contain the expected content");
        assertTrue(document.get(0).getMetadata().get("Title").contains("Test Document"), "Response should contain the expected content");
        assertEquals(testFile.getAbsolutePath(), document.get(0).getMetadata().get("Source"));
    }

    @Test
    void testLoadDataUrl() throws MalformedURLException {
        URL url = new URL(HTML_FILE_URL);
        document = loader.loadData(HTML_FILE_URL);

        assertNotNull(document, "Loaded data must not be null");
        assertTrue(document.get(0).getText().contains("HTML content"), "Response should contain the expected content");
        assertTrue(document.get(0).getMetadata().get("Title").contains("Test Document"), "Response should contain the expected content");
        assertEquals(HTML_FILE_URL, document.get(0).getMetadata().get("Source"));
    }
}
