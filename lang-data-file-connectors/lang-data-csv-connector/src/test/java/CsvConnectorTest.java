import ai.langframework.langdatacsv.CsvConnector;
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

import static ai.langframework.langdatacore.PathFileConstants.CSV_FILE_URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CsvConnectorTest {
    CsvConnector loader;
    List<Document> documents;
    @BeforeAll
    void initializeclient(){
        loader = new CsvConnector();
    }

    @Test
    void testLoadData() throws Exception {
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

            // Load data using the file
            documents = loader.loadData(testFile);
        }

        assertNotNull(documents, "Loaded data must not be null");
        assertTrue(documents.get(0).getText().contains("MySQL Database Connector"), "Response should contain the expected content");
        assertEquals(testFile.getName(), documents.get(0).getMetadata().get("FileName"));
        assertEquals(testFile.getAbsolutePath(), documents.get(0).getMetadata().get("Source"));
        assertEquals(String.valueOf(testFile.length()), documents.get(0).getMetadata().get("FileSize"));
    }

    @Test
    void testLoadDataUrl() throws MalformedURLException {
        URL url = new URL(CSV_FILE_URL);
        CsvConnector loader = new CsvConnector();
        documents = loader.loadData(CSV_FILE_URL);

        assertNotNull(documents, "Loaded data must not be null");
        assertTrue(documents.get(0).getText().contains("MySQL Database Connector"), "Response should contain the expected content");
        assertEquals(url.getFile(), documents.get(0).getMetadata().get("FileName"));
        assertEquals(CSV_FILE_URL, documents.get(0).getMetadata().get("Source"));
        assertEquals(String.valueOf(url.getFile().length()), documents.get(0).getMetadata().get("FileSize"));
    }
}
