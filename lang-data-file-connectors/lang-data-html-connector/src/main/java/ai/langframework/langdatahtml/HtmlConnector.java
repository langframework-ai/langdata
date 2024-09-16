package ai.langframework.langdatahtml;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.Document;

import java.io.File;
import java.util.List;

/**
 * HTML Connector
 */
public class HtmlConnector implements Connector {

    /**
     * Loads data from an HTML file using an HtmlConnectorClient.
     * @param file the HTML file to load data from
     * @return a list of documents containing the data from the HTML file
     */
    @Override
    public List<Document> loadData(File file) {
        return new HtmlConnectorClient().readData(file);
    }
    /**
     * Loads data from an HTML file located at the specified link using an HtmlConnectorClient.
     * @param link the link to the HTML file
     * @return a list of documents containing the data from the HTML file
     */
    @Override
    public List<Document> loadData(String link) {
        return new HtmlConnectorClient().readData(link);
    }

}
