package ai.langframework.langdataopenai;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;
import com.theokanning.openai.service.OpenAiService;

import java.io.File;
import java.util.List;

/**
 * OpenAI Connector
 */
public class OpenAIConnector implements Connector {

    private ConnectorClient connectorClient;

    private String apiKey;

    /**
     * Constructs an OpenAIConnector with no API key.
     * The API key must be set separately using setApiKey() method.
     */
    public OpenAIConnector(){
        connectorClient = new OpenAIConnectorClient();
    }

    /**
     * Constructs an OpenAIConnector with the provided API key.
     * @param api the API key for authentication
     */
    public OpenAIConnector(String api){
        connectorClient = new OpenAIConnectorClient(api);
    }

    /**
     * Sets the API key for the OpenAIConnector.
     * @param apiKey the API key to set
     */
    public void setApiKey(String apiKey){
        this.apiKey = apiKey;
    }

    /**
     * Initializes the OpenAIConnector with the provided API key.
     */
    public void initializeClient(){
        connectorClient = new OpenAIConnectorClient(apiKey);
    }

    /**
     * Loads data using the OpenAIConnectorClient from a file.
     * @param file the file to load data from
     * @return a list of documents containing the data from the file
     * @throws LoaderException if there is an issue loading the data
     */
    @Override
    public List<Document> loadData(File file) throws LoaderException {
        return connectorClient.readData(file);
    }

    /**
     * Loads data using the OpenAIConnectorClient from a link.
     * @param link the link to load data from
     * @return a list of documents containing the data from the link
     * @throws LoaderException if there is an issue loading the data
     */
    @Override
    public List<Document> loadData(String link) throws LoaderException {
        return connectorClient.readData(link);
    }

    /**
     * Retrieves the underlying OpenAI service client instance.
     * @return the OpenAI service client instance
     */
    public OpenAiService getSourceClient(){
        return (OpenAiService)connectorClient.getClient();
    }

    /**
     * Retrieves the OpenAIConnectorClient instance.
     * @return the OpenAIConnectorClient instance
     */
    public OpenAIConnectorClient getConnectorClient(){
        return (OpenAIConnectorClient) connectorClient;
    }


}
