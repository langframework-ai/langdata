import ai.langframework.langdatajson.JsonConnector;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.PathFileConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JsonConnectorTest {
    JsonConnector loader;
    List<Document> document;

    @BeforeAll
    void initializeclient(){
        loader = new JsonConnector();
    }

    @Test
    void loadData() throws Exception {
        File testFile;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test.json")) {
            assertNotNull(inputStream, "Input stream must not be null");

            // Create a temporary file from the input stream
            Path tempFilePath = Files.createTempFile("dataset-", ".json");
            Files.copy(inputStream, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            testFile = tempFilePath.toFile();

            // Ensure to delete the temp file on exit
            testFile.deleteOnExit();

            // Load data using the file
            document = loader.loadData(testFile);
        }

        assertNotNull(document, "Loaded data must not be null");
        assertTrue(document.get(0).getText().contains("name"), "Response should contain the expected content");
        assertEquals(testFile.getName(), document.get(0).getMetadata().get("FileName"));
        assertEquals(testFile.getAbsolutePath(), document.get(0).getMetadata().get("Source"));
        assertEquals(String.valueOf(testFile.length()), document.get(0).getMetadata().get("FileSize"));

    }

    @Test
    void testLoadData() throws Exception{
        URL url = new URL(PathFileConstants.JSON_FILE_URL);
        document = loader.loadData(PathFileConstants.JSON_FILE_URL);

        assertNotNull(document, "Loaded data must not be null");
        assertTrue(document.get(0).getText().contains("version"), "Response should contain the expected content");
        assertEquals(url.getFile(), document.get(0).getMetadata().get("FileName"));
        Assertions.assertEquals(PathFileConstants.JSON_FILE_URL, document.get(0).getMetadata().get("Source"));
        assertEquals(String.valueOf(url.getFile().length()), document.get(0).getMetadata().get("FileSize"));
    }
}