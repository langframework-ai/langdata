package ai.langframework.langdatapinecone;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.VectorInterface;
import ai.langframework.langdatacore.exceptions.LoaderException;
import io.pinecone.PineconeConnection;
import java.io.File;
import java.util.List;

public class PineconeConnector implements Connector {

  private ConnectorClient pineconeClient;
  private String apiKey;
  private String environment;
  private String projectName;
  private String index;
  private String connectionUrl;
  private String modelApiKey;
  private VectorInterface.EmbeddingClientType embeddingClientType;

  /** Constructor initializing PineconeConnector with default settings. */
  public PineconeConnector() {
    pineconeClient = null;
  }

  /**
   * Constructor initializing PineconeConnector with specified API key, environment, project name,
   * index, and connection URL.
   *
   * @param apikey The API key for authentication.
   * @param environment The environment of pinecone index.
   * @param projectName The name of the pinecone project.
   * @param index The pinecone index.
   * @param connectionUrl The connection URL for the Pinecone connector client.
   */
  public PineconeConnector(
      String apikey, String environment, String projectName, String index, String connectionUrl) {
    pineconeClient =
        new PineconeConnectorClient(apikey, environment, projectName, index, connectionUrl);
  }

  /**
   * Constructor initializing PineconeConnector with specified API key, environment, project name,
   * index, connection URL, embedding client type, and model API key.
   *
   * @param apikey The API key for authentication.
   * @param environment The environment of pinecone index.
   * @param projectName The name of the pinecone project.
   * @param index The pinecone index.
   * @param connectionUrl The connection URL for the Pinecone connector client.
   * @param embeddingClientType The type of embedding client.
   * @param modelApiKey The API key for the embedding model.
   */
  public PineconeConnector(
      String apikey,
      String environment,
      String projectName,
      String index,
      String connectionUrl,
      VectorInterface.EmbeddingClientType embeddingClientType,
      String modelApiKey) {
    pineconeClient =
        new PineconeConnectorClient(
            apikey,
            environment,
            projectName,
            index,
            connectionUrl,
            embeddingClientType,
            modelApiKey);
  }

  /**
   * Sets the index for the Pinecone connector client.
   *
   * @param index The index to set.
   */
  public void setIndex(String index) {
    this.index = index;
  }

  /**
   * Sets the connection URL for the Pinecone connector client.
   *
   * @param connectionUrl The connection URL to set.
   */
  public void setConnectionUrl(String connectionUrl) {
    this.connectionUrl = connectionUrl;
  }

  /**
   * Sets the API key for authentication.
   *
   * @param apiKey The API key to set.
   */
  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  /**
   * Sets the pinecone environment.
   *
   * @param environment The environment to set.
   */
  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  /**
   * Sets the project name.
   *
   * @param projectName The project name to set.
   */
  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  /**
   * Sets the embedding client type.
   *
   * @param embeddingClientType The embedding client type to set.
   */
  public void setEmbeddingClientType(VectorInterface.EmbeddingClientType embeddingClientType) {
    this.embeddingClientType = embeddingClientType;
  }

  /**
   * Sets the model API key for the embedding model.
   *
   * @param modelApiKey The model API key to set.
   */
  public void setModelApiKey(String modelApiKey) {
    this.modelApiKey = modelApiKey;
  }

  /** Initializes the Pinecone client. */
  public void initializeClient() {
    if (modelApiKey == null) {
      pineconeClient =
          new PineconeConnectorClient(apiKey, environment, projectName, index, connectionUrl);
    } else {
      pineconeClient =
          new PineconeConnectorClient(
              apiKey,
              environment,
              projectName,
              index,
              connectionUrl,
              embeddingClientType,
              modelApiKey);
    }
  }

  /**
   * Loads data from a file using the PineconeConnectorClient.
   *
   * @param file the file to load data from
   * @return the document containing loaded data
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(File file) throws LoaderException {
    return pineconeClient.readData(file);
  }

  /**
   * Loads data from a link using the PineconeConnectorClient.
   *
   * @param link the link to load data from
   * @return the document containing loaded data
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(String link) throws LoaderException {
    return pineconeClient.readData(link);
  }

  /**
   * Retrieves the underlying PineconeConnection instance.
   *
   * @return the PineconeConnection instance
   */
  public PineconeConnection getSourceClient() {
    return (PineconeConnection) pineconeClient.getClient();
  }

  /**
   * Retrieves the PineconeConnectorClient instance.
   *
   * @return the PineconeConnectorClient instance
   */
  public PineconeConnectorClient getConnectorClient() {
    return (PineconeConnectorClient) pineconeClient;
  }
}
