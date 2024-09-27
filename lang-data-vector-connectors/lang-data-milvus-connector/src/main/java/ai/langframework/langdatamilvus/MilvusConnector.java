package ai.langframework.langdatamilvus;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.VectorInterface;
import ai.langframework.langdatacore.exceptions.LoaderException;
import io.milvus.client.MilvusServiceClient;
import java.io.File;
import java.util.List;

/** Milvus Connector */
public class MilvusConnector implements Connector {

  private ConnectorClient MilvusConnectorClient;
  private String clusterEndpoint;
  private String token;
  private VectorInterface.EmbeddingClientType embeddingClientType;
  private String modelApiKey;

  /** Default constructor for MilvusConnector. */
  public MilvusConnector() {
    MilvusConnectorClient = null;
  }

  /**
   * Constructor for MilvusConnector with clusterEndpoint and token.
   *
   * @param clusterEndpoint The cluster endpoint.
   * @param token The token for authentication.
   */
  public MilvusConnector(String clusterEndpoint, String token) {
    MilvusConnectorClient = new MilvusConnectorClient(clusterEndpoint, token);
  }

  public MilvusConnector(
      String clusterEndpoint,
      String token,
      VectorInterface.EmbeddingClientType embeddingClientType,
      String modelApiKey) {
    MilvusConnectorClient =
        new MilvusConnectorClient(clusterEndpoint, token, embeddingClientType, modelApiKey);
  }

  /**
   * Sets the cluster endpoint.
   *
   * @param clusterEndpoint The cluster endpoint.
   */
  public void setEndpoint(String clusterEndpoint) {
    this.clusterEndpoint = clusterEndpoint;
  }

  /**
   * Sets the token for authentication.
   *
   * @param token The token for authentication.
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * Sets the type of embedding client.
   *
   * @param embeddingClientType the embedding client type to set
   */
  public void setEmbeddingClientType(VectorInterface.EmbeddingClientType embeddingClientType) {
    this.embeddingClientType = embeddingClientType;
  }

  /**
   * Sets the API key for the embedding model.
   *
   * @param modelApiKey the model API key to set
   */
  public void setModelApiKey(String modelApiKey) {
    this.modelApiKey = modelApiKey;
  }

  /** Initializes the MilvusConnectorClient with the provided cluster endpoint and token. */
  public void initializeClient() {
    if (modelApiKey == null) {
      MilvusConnectorClient = new MilvusConnectorClient(clusterEndpoint, token);
    } else {
      MilvusConnectorClient =
          new MilvusConnectorClient(clusterEndpoint, token, embeddingClientType, modelApiKey);
    }
  }

  /**
   * Loads data from a file.
   *
   * @param file the file to load data from
   * @return a list of documents containing the data from the file
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(File file) throws LoaderException {
    return null;
  }

  /**
   * Loads data from a link.
   *
   * @param link the link to load data from
   * @return a list of documents containing the data from the link
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(String link) throws LoaderException {
    return MilvusConnectorClient.readData(link);
  }

  /**
   * Retrieves the source client.
   *
   * @return The MilvusServiceClient instance.
   */
  public MilvusServiceClient getSourceClient() {
    return (MilvusServiceClient) MilvusConnectorClient.getClient();
  }

  /**
   * Retrieves the ConnectorClient instance.
   *
   * @return The MilvusConnectorClient instance.
   */
  public MilvusConnectorClient getConnectorClient() {
    return (MilvusConnectorClient) MilvusConnectorClient;
  }
}
