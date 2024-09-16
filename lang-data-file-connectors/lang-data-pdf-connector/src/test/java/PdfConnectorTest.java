import ai.langframework.langdatapdf.PdfConnector;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.PathFileConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PdfConnectorTest {
    private static final String PDF_FILE_PATH = "test.pdf";
    private static final String PDF_URL = PathFileConstants.PDF_FILE_URL;
    private PdfConnector loader;
    private File testFile;
    private URL url;
    List<Document> document;

    @BeforeEach
    void setUp() throws Exception {
        loader = new PdfConnector();

        URL resourceUrl = getClass().getResource("/" + PDF_FILE_PATH);
        if (resourceUrl == null) {
            throw new IllegalStateException("test.pdf not found in classpath");
        }
        testFile = new File(resourceUrl.toURI());
        url = new URL(PDF_URL);
    }


    @Test
    void testLoadDataFile() {
        document = loader.loadData(testFile);

        assertNotNull(document, "Loaded data must not be null");
        assertTrue(document.get(0).getText().toLowerCase().contains("test"), "Response should contain the expected content");
        assertEquals(testFile.getName(), document.get(0).getMetadata().get("FileName"));
        assertEquals(testFile.getAbsolutePath(), document.get(0).getMetadata().get("Source"));
        assertEquals(String.valueOf(testFile.length()), document.get(0).getMetadata().get("FileSize"));
    }

    @Test
    void testLoadDataUrl() {
        document = loader.loadData(PDF_URL);

        assertNotNull(document, "Loaded data must not be null");
        assertTrue(document.get(0).getText().toLowerCase().contains("test"), "Response should contain the expected content");
        assertEquals(url.getFile(), document.get(0).getMetadata().get("FileName"));
        assertEquals(PDF_URL, document.get(0).getMetadata().get("Source"));
        // Assuming the loader can determine the size of the file from URL and store it
        assertNotNull(document.get(0).getMetadata().get("FileSize"), "FileSize metadata should be set");
        assertEquals(String.valueOf(url.getFile().length()), document.get(0).getMetadata().get("FileSize"));
    }
}
