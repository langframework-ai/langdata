package ai.langframework.langdatasqldatabase;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;
import java.io.File;
import java.sql.Connection;
import java.util.List;

/** DatabaseSql Connector */
public class DatabaseSqlConnector implements Connector {

  private ConnectorClient databaseSqlConnectorClient;

  private String driver;
  private String url;
  private String username;
  private String password;

  /**
   * This constructor initializes a `DatabaseSqlConnectorClient` object with the provided driver,
   * URL, username, and password parameters.
   *
   * @param driver
   * @param url
   * @param username
   * @param password
   */
  public DatabaseSqlConnector(String driver, String url, String username, String password) {
    databaseSqlConnectorClient = new DatabaseSqlConnectorClient(driver, url, username, password);
  }

  /** This constructor initializes a `DatabaseSqlConnectorClient` object without any parameters. */
  public DatabaseSqlConnector() {
    databaseSqlConnectorClient = new DatabaseSqlConnectorClient();
  }

  /**
   * Loads data using the DatabaseSqlConnectorClient from a file.
   *
   * @param file the file to load data from
   * @return a list of documents containing the data from the file
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(File file) throws LoaderException {
    return databaseSqlConnectorClient.readData(file);
  }

  /**
   * Loads data using the DatabaseSqlConnectorClient from a link.
   *
   * @param link the link to load data from
   * @return a list of documents containing the data from the link
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(String link) throws LoaderException {
    return databaseSqlConnectorClient.readData(link);
  }

  /**
   * This method sets the driver for the database connection.
   *
   * @param driver
   */
  public void setDriver(String driver) {
    this.driver = driver;
  }

  /**
   * This method sets the url for the database connection.
   *
   * @param url
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * This method sets the username for the database connection.
   *
   * @param username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * This method sets the password for the database connection.
   *
   * @param password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * This method initializes the client for the database SQL connector with the specified driver,
   * URL, username, and password.
   */
  public void initializeClient() {
    databaseSqlConnectorClient = new DatabaseSqlConnectorClient(driver, url, username, password);
  }

  /**
   * This method retrieves the source client from the database SQL connector.
   *
   * @return
   */
  public Connection getSourceClient() {
    return (Connection) databaseSqlConnectorClient.getClient();
  }

  /**
   * This method retrieves the connector client from the database SQL connector.
   *
   * @return
   */
  public DatabaseSqlConnectorClient getConnectorClient() {
    return (DatabaseSqlConnectorClient) databaseSqlConnectorClient;
  }
}
