package ai.langframework.langdatas3;

import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;
import ai.langframework.langdatacsv.CsvConnector;
import ai.langframework.langdatadocx.DocxConnector;
import ai.langframework.langdatahtml.HtmlConnector;
import ai.langframework.langdatajson.JsonConnector;
import ai.langframework.langdatapdf.PdfConnector;
import ai.langframework.langdatapptx.PptxConnector;
import ai.langframework.langdatatext.TextConnector;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.util.List;

/** S3Connector Client */
public class S3ConnectorClient implements ConnectorClient {
  AmazonS3 s3client;

  /** Default constructor for S3ConnectorClient. */
  public S3ConnectorClient() {

    s3client = null;
  }

  /**
   * Constructor for S3ConnectorClient with provided access key and secret key.
   *
   * @param accessKey the access key for AWS authentication
   * @param secretKey the secret key for AWS authentication
   */
  public S3ConnectorClient(String accessKey, String secretKey) {
    AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    s3client =
        AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.US_EAST_1)
            .build();
  }

  /**
   * Reads data from a specific object in an S3 bucket.
   *
   * @param bucketName the name of the S3 bucket
   * @param objectName the name of the object in the bucket
   * @return a list of documents
   */
  public List<Document> readData(String bucketName, String objectName) {

    try {
      String prefixName = objectName.substring(objectName.lastIndexOf(".") + 1);
      List<Document> document;
      switch (prefixName.toUpperCase()) {
        case "CSV":
          document = new CsvConnector().loadData(generatePresignedUrl(bucketName, objectName));
          break;
        case "HTML":
          document = new HtmlConnector().loadData(generatePresignedUrl(bucketName, objectName));
          break;
        case "PDF":
          document = new PdfConnector().loadData(generatePresignedUrl(bucketName, objectName));
          break;
        case "TXT":
        case "MD":
          document = new TextConnector().loadData(generatePresignedUrl(bucketName, objectName));
          break;
        case "DOCX":
        case "DOTX":
          document = new DocxConnector().loadData(generatePresignedUrl(bucketName, objectName));
          break;
        case "PPTX":
          document = new PptxConnector().loadData(generatePresignedUrl(bucketName, objectName));
          break;
        case "JSON":
          document = new JsonConnector().loadData(generatePresignedUrl(bucketName, objectName));
          break;
        default:
          throw new IllegalArgumentException(
              "Unsupported file connector type: " + "fileConnectorType");
      }
      return document;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Uploads a file to the specified bucket and object in Amazon S3.
   *
   * @param bucketName the name of the bucket in Amazon S3
   * @param objectName the name of the object in the bucket
   * @param file the file to upload
   */
  public void uploadFile(String bucketName, String objectName, File file) {
    try {
      s3client.putObject(new PutObjectRequest(bucketName, objectName, file));
      System.out.println("File uploaded successfully to S3!");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Retrieves the underlying Amazon S3 client instance.
   *
   * @return the Amazon S3 client instance
   */
  @Override
  public Object getClient() {
    return s3client;
  }

  /**
   * This functionality is not available for this client.
   *
   * @param file the file to read data from
   * @return a list of documents
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> readData(File file) throws LoaderException {
    return null;
  }

  /**
   * This functionality is not available for this client.
   *
   * @param link the link to read data from
   * @return a list of documents
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> readData(String link) throws LoaderException {
    return null;
  }

  /**
   * Initializes the connector with the provided access key and secret key.
   *
   * @param accessKey the access key for AWS authentication
   * @param secretKey the secret key for AWS authentication
   */
  public void initializeClient(String accessKey, String secretKey) {
    AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    s3client =
        AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.US_EAST_1)
            .build();
  }

  /**
   * Generates a pre-signed URL for accessing an object in an S3 bucket.
   *
   * @param bucketName the name of the S3 bucket
   * @param objectKey the key of the object in the bucket
   * @return the pre-signed URL
   */
  public String generatePresignedUrl(String bucketName, String objectKey) {
    // Set the expiration time for the URL (in milliseconds)
    // Here we set it to expire in 1 hour
    java.util.Date expiration = new java.util.Date();
    long expTimeMillis = expiration.getTime();
    expTimeMillis += 1000 * 60 * 60; // 1 hour
    expiration.setTime(expTimeMillis);

    // Generate the pre-signed URL
    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(bucketName, objectKey)
            .withMethod(HttpMethod.GET)
            .withExpiration(expiration);
    return s3client.generatePresignedUrl(generatePresignedUrlRequest).toString();
  }
}
