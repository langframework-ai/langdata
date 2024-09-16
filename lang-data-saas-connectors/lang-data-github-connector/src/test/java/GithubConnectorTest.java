import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.PathFileConstants;
import ai.langframework.langdatagithub.GithubConnector;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GithubConnectorTest {

    private GithubConnector loader;

    @BeforeAll
    void initializeclient(){
        loader = new GithubConnector();
    }

    @Test
    void testExtreactRepoContent() throws Throwable {
        List<Document> document = loader.loadData(PathFileConstants.GITHUB_ISSUE_URL);
        assertNotNull(document, "Loaded data must not be null");
        assertEquals(PathFileConstants.GITHUB_ISSUE_URL, document.get(0).getMetadata().get("Source"));
        assertTrue(document.get(0).getText().contains("Learning"), "Expected content not loaded");
    }
}
