package ai.langframework.langdatapgvector;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.VectorInterface;
import ai.langframework.langdatacore.exceptions.LoaderException;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/** Pgvector Connector */
public class PgvectorConnector implements Connector {

  private ConnectorClient pgvectorConnectorClient;
  private String driver;
  private String url;
  private String username;
  private String password;
  private VectorInterface.EmbeddingClientType embeddingClientType;
  private String modelApiKey;

  /**
   * Constructs a PgvectorConnector with the specified database connection parameters and model API
   * key.
   *
   * @param driver the JDBC driver class name
   * @param url the database URL
   * @param username the username for database authentication
   * @param password the password for database authentication
   * @param embeddingClientType the type of embedding client
   * @param modelApiKey the API key for the embedding model
   * @throws SQLException if a database access error occurs
   */
  public PgvectorConnector(
      String driver,
      String url,
      String username,
      String password,
      VectorInterface.EmbeddingClientType embeddingClientType,
      String modelApiKey)
      throws SQLException {
    pgvectorConnectorClient =
        new PgvectorConnectorClient(
            driver, url, username, password, embeddingClientType, modelApiKey);
  }

  /** Constructs a PgvectorConnector with default settings. */
  public PgvectorConnector() {
    pgvectorConnectorClient = new PgvectorConnectorClient();
  }

  /**
   * Sets the JDBC driver class name.
   *
   * @param driver the JDBC driver class name to set
   */
  public void setDriver(String driver) {
    this.driver = driver;
  }

  /**
   * Sets the database URL.
   *
   * @param url the database URL to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Sets the username for database authentication.
   *
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Sets the password for database authentication.
   *
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
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

  /**
   * Initializes the PgvectorConnector with the provided database connection parameters and model
   * API key.
   *
   * @throws SQLException if a database access error occurs
   */
  public void initializeClient() throws SQLException {
    pgvectorConnectorClient =
        new PgvectorConnectorClient(
            driver, url, username, password, embeddingClientType, modelApiKey);
  }

  /**
   * Retrieves the underlying database connection.
   *
   * @return the database connection
   */
  public Connection getSourceClient() {
    return (Connection) pgvectorConnectorClient.getClient();
  }

  /**
   * Retrieves the PgvectorConnectorClient instance.
   *
   * @return the PgvectorConnectorClient instance
   */
  public PgvectorConnectorClient getConnectorClient() {
    return (PgvectorConnectorClient) pgvectorConnectorClient;
  }

  /**
   * Loads data using the PgvectorConnectorClient from a file.
   *
   * @param file the file to load data from
   * @return a list of documents containing the data from the file
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(File file) throws LoaderException {
    return pgvectorConnectorClient.readData(file);
  }

  /**
   * Loads data using the PgvectorConnectorClient from a link.
   *
   * @param link the link to load data from
   * @return a list of documents containing the data from the link
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(String link) throws LoaderException {
    return pgvectorConnectorClient.readData(link);
  }
}
