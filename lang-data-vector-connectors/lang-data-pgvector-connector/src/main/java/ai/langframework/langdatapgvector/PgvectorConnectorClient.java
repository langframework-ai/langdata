package ai.langframework.langdatapgvector;

import ai.langframework.langdatacohereai.CohereAIConnectorClient;
import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.VectorInterface;
import ai.langframework.langdatacore.exceptions.LoaderException;
import ai.langframework.langdataopenai.OpenAIConnectorClient;
import com.pgvector.PGvector;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Pgvector Connector Client */
public class PgvectorConnectorClient implements ConnectorClient, VectorInterface {

  private Connection connection;
  private EmbeddingClientType embeddingClientType;
  private OpenAIConnectorClient modelOpenAIConnectorClient;
  private CohereAIConnectorClient modelCohereConnectorClient;
  private String tableName;
  private String embeddingModel = "text-embedding-ada-002";
  private int limit = 5;

  /** Constructs a new PgvectorConnectorClient with a null connection. */
  public PgvectorConnectorClient() {
    connection = null;
  }

  /**
   * This "PgvectorConnectorClient" constructor initializes a connection to a PostgreSQL database
   * using the provided driver, URL, username, and password. It also enables the "vector" extension
   * and registers the vector type with the database connection, allowing for operations on vector
   * data.
   *
   * @param driver
   * @param url
   * @param username
   * @param password
   */
  public PgvectorConnectorClient(
      String driver,
      String url,
      String username,
      String password,
      EmbeddingClientType embeddingClientType,
      String modelApiKey)
      throws SQLException {
    connection = null;
    try {
      Class.forName(driver);
      connection = DriverManager.getConnection(url, username, password);

      this.embeddingClientType = embeddingClientType;

      switch (embeddingClientType) {
        case OPENAI:
          modelOpenAIConnectorClient = new OpenAIConnectorClient();
          modelOpenAIConnectorClient.initializeClient(modelApiKey);
          break;
        case COHERE:
          modelCohereConnectorClient = new CohereAIConnectorClient();
          modelCohereConnectorClient.initializeClient(modelApiKey);
          break;
        default:
          throw new Exception("Unsupported Embedding Client Type");
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    //        Enable the extension
    Statement setupStmt = connection.createStatement();
    setupStmt.executeUpdate("CREATE EXTENSION IF NOT EXISTS vector");
    //         Register the vector type with your connection
    PGvector.addVectorType(connection);
  }

  /**
   * Sets the embedding model for the Pgvector connector client.
   *
   * @param embeddingModel The embedding model to set.
   */
  public void setEmbeddingModel(String embeddingModel) {
    this.embeddingModel = embeddingModel;
  }

  /**
   * Sets the number of records to retrieve
   *
   * @param limit
   */
  public void setLimit(int limit) {
    this.limit = limit;
  }

  /**
   * Sets the table name for the PgvectorConnectorClient.
   *
   * @param tableName the name of the table to be set
   */
  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  /**
   * This "createTable" method dynamically creates a table with the specified name and columns along
   * with their data types. It also adds a primary key constraint to the "id" column, if present,
   * and returns a message indicating the success of the operation.
   *
   * @param tableName
   * @param columns
   * @throws SQLException
   */
  public String createTable(String tableName, Map<String, String> columns) throws SQLException {
    StringBuilder queryBuilder = new StringBuilder("CREATE TABLE " + tableName + " (");

    // Append columns with their data types
    for (Map.Entry<String, String> entry : columns.entrySet()) {
      queryBuilder.append(entry.getKey()).append(" ").append(entry.getValue()).append(", ");
    }

    // Remove the trailing comma and space
    queryBuilder.setLength(queryBuilder.length() - 2);

    // Add primary key constraint
    queryBuilder.append(", PRIMARY KEY (id))");

    // Execute the query
    try (Statement createStmt = connection.createStatement()) {
      createStmt.executeUpdate(queryBuilder.toString());
    }
    return ("Created Successfully");
  }

  /**
   * This "insertData" method dynamically inserts data into the specified table using the provided
   * column names and values, and returns a message indicating the success of the operation.
   *
   * @param tableName
   * @param columnValues
   * @throws SQLException
   */
  public String insertData(String tableName, Map<String, Object> columnValues) throws SQLException {
    // Construct the query
    StringBuilder queryBuilder = new StringBuilder("INSERT INTO " + tableName + " (");
    StringBuilder valueBuilder = new StringBuilder(" VALUES (");

    // Prepare column names and placeholders
    for (String columnName : columnValues.keySet()) {
      queryBuilder.append(columnName).append(", ");
      valueBuilder.append("?, ");
    }

    // Remove trailing comma and space
    queryBuilder.setLength(queryBuilder.length() - 2);
    valueBuilder.setLength(valueBuilder.length() - 2);

    // Complete the query
    queryBuilder.append(")").append(valueBuilder).append(")");

    // Prepare and execute the statement
    try (PreparedStatement insertStmt = connection.prepareStatement(queryBuilder.toString())) {
      int parameterIndex = 1;

      // Set parameter values
      for (Object value : columnValues.values()) {
        insertStmt.setObject(parameterIndex++, value);
      }

      insertStmt.executeUpdate();
    }

    return ("Inserted Successfully");
  }

  /**
   * This "getNearestNeighbors" method retrieves nearest neighbors from the specified table for a
   * given query vector, limited by the provided count, and returns them as a string.
   *
   * @param tableName
   * @param queryVector
   * @param limit
   * @throws SQLException
   */
  private ResultSet getNN(String tableName, float[] queryVector, int limit) throws SQLException {
    String query = "SELECT * FROM " + tableName + " ORDER BY vector <-> ? LIMIT ?";
    PreparedStatement neighborStmt = connection.prepareStatement(query);
    neighborStmt.setObject(1, new PGvector(queryVector));
    neighborStmt.setInt(2, limit);
    return neighborStmt.executeQuery();
  }

  /**
   * Retrieves nearest neighbors from the specified table for a given query vector, limited by the
   * provided count
   *
   * @param tableName
   * @param queryVector
   * @param limit
   * @return List<Map<String, Object>>
   * @throws SQLException
   */
  public List<Map<String, Object>> getNearestNeighborsAsMap(
      String tableName, float[] queryVector, int limit) throws SQLException {
    List<Map<String, Object>> result = new ArrayList<>();

    try (ResultSet rs = getNN(tableName, queryVector, limit)) {
      ResultSetMetaData metaData = rs.getMetaData();
      int columnCount = metaData.getColumnCount();

      while (rs.next()) {
        Map<String, Object> row = new HashMap<>();
        for (int i = 1; i <= columnCount; i++) {
          String columnName = metaData.getColumnName(i);
          Object columnValue = rs.getObject(i);
          row.put(columnName, columnValue);
        }
        result.add(row);
      }
    }

    return result;
  }

  /**
   * The "addIndex" function dynamically creates an index for the specified table using the provided
   * index type, indexing method, and optional additional options.
   *
   * @param tableName
   * @param indexType
   * @param indexingMethod
   * @param additionalOptions
   * @throws SQLException
   */
  public String addIndex(
      String tableName, String indexType, String indexingMethod, String additionalOptions)
      throws SQLException {
    String query =
        "CREATE INDEX ON " + tableName + " USING " + indexType + " (" + indexingMethod + ")";

    if (additionalOptions != null && !additionalOptions.isEmpty()) {
      query += " WITH (" + additionalOptions + ")";
    }

    try (Statement indexStmt = connection.createStatement()) {
      indexStmt.executeUpdate(query);
    }

    return ("Created Successfully");
  }

  /**
   * Deleted a table from the database
   *
   * @param tableName
   * @return Success message
   * @throws SQLException
   */
  public String deleteTable(String tableName) throws SQLException {
    String query = "DROP TABLE " + tableName;

    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.executeUpdate();
    }

    return ("Table Dropped Successfully");
  }

  /**
   * Retrieves the client object associated with the PgvectorConnectorClient.
   *
   * @return the client object
   */
  @Override
  public Object getClient() {
    return connection;
  }

  /**
   * This functionality is not available for this client.
   *
   * @param file the file from which to read the data
   * @return a list of documents read from the file
   * @throws LoaderException if an error occurs during the data loading process
   */
  @Override
  public List<Document> readData(File file) throws LoaderException {
    return null;
  }

  /**
   * This functionality is not available for this client.
   *
   * @param link the link from which to read the data
   * @return a list of documents read from the link
   * @throws LoaderException if an error occurs during the data loading process
   */
  @Override
  public List<Document> readData(String link) throws LoaderException {
    return null;
  }

  /**
   * Accepts a list of documents, convert it onto vectors and stores it into Database
   *
   * @param documents
   */
  @Override
  public void addDocuments(List<Document> documents) {
    try {
      if (tableName == null) {
        throw new Exception("Table Name is not set");
      }
      if (embeddingClientType == EmbeddingClientType.OPENAI) {

        for (Document doc : documents) {
          Map<String, Object> columnValues = new HashMap<>();

          List<Float[]> data =
              modelOpenAIConnectorClient.embedding(List.of(doc.getText()), embeddingModel);

          columnValues.put("vector", new PGvector(convertFirstArrayToPrimitive(data)));
          columnValues.put("text", doc.getText());

          insertData(tableName, columnValues);
        }
      } else if (embeddingClientType == EmbeddingClientType.COHERE) {

        for (Document doc : documents) {
          Map<String, Object> columnValues = new HashMap<>();

          Float[] data = modelCohereConnectorClient.embedText(doc.getText());

          float[] floatArray = new float[data.length];

          for (int i = 0; i < data.length; i++) {
            floatArray[i] = data[i];
          }

          columnValues.put("vector", new PGvector(floatArray));
          columnValues.put("text", doc.getText());

          insertData(tableName, columnValues);
        }
      } else {
        throw new Exception("Unsupported Embedding Client Type");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns a list of top 5 documents that are similar to the query passed
   *
   * @param query
   * @return
   */
  @Override
  public List<Document> searchSimilarity(String query) {
    List<Document> documents = new ArrayList<>();
    try {
      if (tableName == null) {
        throw new Exception("table name is not set");
      }
      if (embeddingClientType == EmbeddingClientType.OPENAI) {

        List<Float[]> data = modelOpenAIConnectorClient.embedding(List.of(query), embeddingModel);

        ResultSet response = getNN(tableName, convertFirstArrayToPrimitive(data), limit);

        while (response.next()) {
          HashMap<String, String> metadata = new HashMap<>();
          String text = response.getObject("text").toString();
          metadata.put("vectors", response.getObject("vector").toString());

          documents.add(new Document(text, metadata));
        }
        return documents;

      } else if (embeddingClientType == EmbeddingClientType.COHERE) {

        Float[] data = modelCohereConnectorClient.embedText(query);

        float[] floatArray = new float[data.length];

        for (int i = 0; i < data.length; i++) {
          floatArray[i] = data[i];
        }

        ResultSet response = getNN(tableName, floatArray, limit);

        while (response.next()) {
          HashMap<String, String> metadata = new HashMap<>();
          String text = response.getObject("text").toString();
          metadata.put("vectors", response.getObject("vector").toString());

          documents.add(new Document(text, metadata));
        }
        return documents;

      } else {
        throw new Exception("Unsupported Embedding Client Type");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Converts the first array in the List of Float array to a primitive float array
   *
   * @param listOfArrays
   * @return
   */
  private float[] convertFirstArrayToPrimitive(List<Float[]> listOfArrays) {
    // Check if the list is not empty
    if (!listOfArrays.isEmpty()) {
      // Get the first Float[] array
      Float[] firstArray = listOfArrays.get(0);

      // Convert the first Float[] array to float[]
      float[] resultArray = new float[firstArray.length];
      for (int i = 0; i < firstArray.length; i++) {
        resultArray[i] = firstArray[i];
      }
      return resultArray;
    } else {
      // If the list is empty, return an empty float array
      return new float[0];
    }
  }
}
