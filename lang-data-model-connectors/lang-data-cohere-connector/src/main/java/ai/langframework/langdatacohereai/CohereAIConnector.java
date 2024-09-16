package ai.langframework.langdatacohereai;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;
import com.cohere.api.Cohere;

import java.io.File;
import java.util.List;

/**
 * CohereAI Connector
 */
public class CohereAIConnector implements Connector {
    private ConnectorClient cohereLangDataClient;
    private String apiKey;

    /**
     * Constructs a CohereAIConnector with the provided API key.
     * @param apiKey the API key for authentication
     */
    public CohereAIConnector(String apiKey){
        cohereLangDataClient = new CohereAIConnectorClient(apiKey);
    }

    /**
     * Constructs a CohereAIConnector with no API key.
     * The API key must be set separately using setApiKey() method.
     */
    public CohereAIConnector(){
        cohereLangDataClient = new CohereAIConnectorClient();
    }

    /**
     * Loads data using the CohereAIConnectorClient from a file.
     * @param file the file to load data from
     * @return a list of documents containing the data from the file
     * @throws LoaderException if there is an issue loading the data
     */
    @Override
    public List<Document> loadData(File file) throws LoaderException {
        return cohereLangDataClient.readData(file);
    }

    /**
     * Loads data using the CohereAIConnectorClient from a link.
     * @param link the link to load data from
     * @return a list of documents containing the data from the link
     * @throws LoaderException if there is an issue loading the data
     */
    @Override
    public List<Document> loadData(String link) throws LoaderException {
        return cohereLangDataClient.readData(link);
    }

    /**
     * Sets the API key for the CohereAIConnector.
     * @param apiKey the API key to set
     */
    public void setApiKey(String apiKey){
        this.apiKey = apiKey;
    }

    /**
     * Initializes the CohereAIConnector with the provided API key.
     */
    public void initializeClient(){
        cohereLangDataClient = new CohereAIConnectorClient(apiKey);
    }

    /**
     * Retrieves the underlying Cohere client instance.
     * @return the Cohere client instance
     */
    public Cohere getSourceClient(){
        return (Cohere) cohereLangDataClient.getClient();
    }

    /**
     * Retrieves the CohereAIConnectorClient instance.
     * @return the CohereAIConnectorClient instance
     */
    public CohereAIConnectorClient getConnectorClient(){
        return (CohereAIConnectorClient) cohereLangDataClient;
    }

}
