package ai.langframework.langdatatext;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.Document;

import java.io.File;
import java.util.List;

/**
 * Text Connector
 */
public class TextConnector implements Connector {
    /**
     * Loads data from a text file using a TextConnectorClient.
     * @param file the text file to load data from
     * @return a list of documents containing the data from the text file
     */
    @Override
    public List<Document> loadData(File file) {
        return new TextConnectorClient().readData(file);
    }

    /**
     * Loads data from a text file located at the specified link using a TextConnectorClient.
     * @param link the link to the text file
     * @return a list of documents containing the data from the text file
     */
    @Override
    public List<Document> loadData(String link){
        return new TextConnectorClient().readData(link);
    }

}
