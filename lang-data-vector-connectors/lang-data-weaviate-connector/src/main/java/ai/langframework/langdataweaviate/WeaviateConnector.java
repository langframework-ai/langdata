package ai.langframework.langdataweaviate;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.VectorInterface;
import ai.langframework.langdatacore.exceptions.LoaderException;
import io.weaviate.client.WeaviateClient;

import java.io.File;
import java.util.List;

/**
 * Weaviate Connector
 */
public class WeaviateConnector implements Connector {
    private ConnectorClient weaviateLangDataClient;
    private String URL;
    private String apiKey;
    private String openAiKey;
    private String modelApiKey;
    private VectorInterface.EmbeddingClientType embeddingClientType;

    /**
     * Constructor initializing WeaviateConnector with default settings.
     */
    public WeaviateConnector(){
        weaviateLangDataClient = new WeaviateConnectorClient();
    }

    /**
     * Constructor initializing WeaviateConnector with the provided URL and API key.
     * @param URL the URL of the Weaviate server
     * @param apiKey the API key for authentication
     */
    public WeaviateConnector(String URL, String apiKey){
         weaviateLangDataClient = new WeaviateConnectorClient(URL, apiKey);
    }

    /**
     * Constructor initializing WeaviateConnector with the provided URL, API key, type of embedding client and embedding model key.
     * @param URL
     * @param apiKey
     * @param embeddingClientType
     * @param modelApiKey
     */
    public WeaviateConnector(String URL, String apiKey, VectorInterface.EmbeddingClientType embeddingClientType, String modelApiKey){
        weaviateLangDataClient = new WeaviateConnectorClient(URL, apiKey, embeddingClientType, modelApiKey);
    }

    /**
     * Constructor initializing WeaviateConnector with the provided URL, API key, and OpenAI key.
     * @param URL the URL of the Weaviate server
     * @param apiKey the API key for authentication
     * @param OpenAIKey the API key for OpenAI integration
     */
    public WeaviateConnector(String URL, String apiKey, String OpenAIKey){
        weaviateLangDataClient = new WeaviateConnectorClient(URL, apiKey, OpenAIKey);
    }
    /**
     * Loads data using the WeaviateConnectorClient from a file.
     * @param file the file to load data from
     * @return a list of documents containing the data from the file
     * @throws LoaderException if there is an issue loading the data
     */
    @Override
    public List<Document> loadData(File file) throws LoaderException {
        return weaviateLangDataClient.readData(file);
    }
    /**
     * Loads data using the WeaviateConnectorClient from a link.
     * @param link the link to load data from
     * @return a list of documents containing the data from the link
     * @throws LoaderException if there is an issue loading the data
     */
    @Override
    public List<Document> loadData(String link) throws LoaderException {
        return weaviateLangDataClient.readData(link);
    }

    /**
     * Sets the URL of the Weaviate server.
     * @param url the URL of the Weaviate server
     */
    public void setUrl(String url){
        this.URL = url;
    }

    /**
     * Sets the API key for authentication.
     * @param apiKey the API key for authentication
     */
    public void setApiKey(String apiKey){
        this.apiKey = apiKey;
    }

    /**
     * Sets the OpenAI key for integration.
     * @param openAiKey the OpenAI key for integration
     */
    public void setOpenAiKey(String openAiKey){
        this.openAiKey = openAiKey;
    }

    /**
     * Sets the type of embedding model
     * @param embeddingClientType
     */
    public void setEmbeddingClientType(VectorInterface.EmbeddingClientType embeddingClientType){
        this.embeddingClientType = embeddingClientType;
    }

    /**
     * Sets the api key for the embedding model
     * @param modelApiKey
     */
    public void setModelApiKey(String modelApiKey){
        this.modelApiKey = modelApiKey;
    }

    /**
     * Initializes the WeaviateConnectorClient with the configured settings.
     * If the OpenAI key is null, initializes with URL and API key only.
     * @see #setUrl(String)
     * @see #setApiKey(String)
     * @see #setOpenAiKey(String)
     */
    public void initializeClient(){
        if(openAiKey == null && modelApiKey==null)
        {
            weaviateLangDataClient = new WeaviateConnectorClient(URL, apiKey);
        }
        else if(openAiKey == null && modelApiKey!=null){
            weaviateLangDataClient = new WeaviateConnectorClient(URL, apiKey, embeddingClientType, modelApiKey);
        }
        else if(openAiKey != null && modelApiKey==null){
            weaviateLangDataClient = new WeaviateConnectorClient(URL, apiKey, openAiKey);
        }
    }

    /**
     * Retrieves the WeaviateClient instance from the underlying WeaviateConnectorClient.
     * @return the WeaviateClient instance
     */
    public WeaviateClient getSourceClient(){
        return (WeaviateClient) weaviateLangDataClient.getClient();
    }

    /**
     * Retrieves the underlying WeaviateConnectorClient instance.
     * @return the WeaviateConnectorClient instance
     */
    public WeaviateConnectorClient getConnectorClient(){
        return (WeaviateConnectorClient) weaviateLangDataClient;
    }
}
