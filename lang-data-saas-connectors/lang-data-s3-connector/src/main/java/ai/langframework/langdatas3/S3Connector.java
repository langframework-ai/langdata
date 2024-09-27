package ai.langframework.langdatas3;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;
import com.amazonaws.services.s3.AmazonS3;
import java.io.File;
import java.util.List;

/** S3 Connector */
public class S3Connector implements Connector {

  private ConnectorClient connectorClient;

  private String accessKey;
  private String secretKey;

  /**
   * Constructs an S3Connector with no access key and secret key. These credentials must be set
   * separately using setAccessKey() and setSecretKey() methods.
   */
  public S3Connector() {
    connectorClient = new S3ConnectorClient();
  }

  /**
   * Constructs an S3Connector with the provided access key and secret key.
   *
   * @param accessKey the access key for AWS authentication
   * @param secretKey the secret key for AWS authentication
   */
  public S3Connector(String accessKey, String secretKey) {
    connectorClient = new S3ConnectorClient(accessKey, secretKey);
  }

  /**
   * Loads data using the S3ConnectorClient from a file.
   *
   * @param file the file to load data from
   * @return a list of documents containing the data from the file
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(File file) throws LoaderException {
    return connectorClient.readData(file);
  }

  /**
   * Loads data using the S3ConnectorClient from a link.
   *
   * @param link the link to load data from
   * @return a list of documents containing the data from the link
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(String link) throws LoaderException {
    return connectorClient.readData(link);
  }

  /**
   * Sets the secret key for the S3Connector.
   *
   * @param secretKey the secret key to set
   */
  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  /**
   * Sets the access key for the S3Connector.
   *
   * @param accessKey the access key to set
   */
  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }

  /** Initializes the S3Connector with the provided access key and secret key. */
  public void initializeClient() {
    connectorClient = new S3ConnectorClient(accessKey, secretKey);
  }

  /**
   * Retrieves the underlying Amazon S3 client instance.
   *
   * @return the Amazon S3 client instance
   */
  public AmazonS3 getClient() {
    return (AmazonS3) connectorClient.getClient();
  }

  /**
   * Retrieves the S3ConnectorClient instance.
   *
   * @return the S3ConnectorClient instance
   */
  public S3ConnectorClient getConnectorClient() {

    return (S3ConnectorClient) connectorClient;
  }
}
