import ai.langframework.langdatacore.Document;
import ai.langframework.langdatawikipedia.WikipediaConnector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WikipediaConnectorTest {
    private static final String ARTICLE_TITLE = "Java (Programming Language)";
    private WikipediaConnector loader;

    @BeforeAll
    void initializeclient(){
        loader = new WikipediaConnector();
    }

    @Test
    void loadData() {
        List<Document> document;
        try {
            document = loader.loadData(ARTICLE_TITLE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        assertNotNull(document);
        assertEquals(ARTICLE_TITLE, document.get(0).getMetadata().get("ArticleTitle"));
    }
}