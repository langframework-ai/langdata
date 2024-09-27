package ai.langframework.langdatasqldatabase;

import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** DatabaseSql Connector Client */
public class DatabaseSqlConnectorClient implements ConnectorClient {

  Connection connection;

  /** Default constructor for Database Sql Connector Client */
  public DatabaseSqlConnectorClient() {
    connection = null;
  }

  /**
   * This constructor creates a database connector client with the given driver, URL, username, and
   * password, attempting to establish a connection to the database. If an exception occurs, it
   * prints the error message.
   *
   * @param driver
   * @param url
   * @param username
   * @param password
   */
  public DatabaseSqlConnectorClient(String driver, String url, String username, String password) {
    connection = null;
    try {
      Class.forName(driver);
      connection = DriverManager.getConnection(url, username, password);
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  /**
   * This function retrieves all rows from a specified database table and returns them as a list of
   * lists.
   *
   * @param tableName
   * @return It returns a list of lists where each inner list represents a row of the query result,
   * @throws SQLException
   */
  public List<List<String>> getTablesRows(String tableName) throws SQLException {
    List<List<String>> result = new ArrayList<>();
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery("select * from " + tableName);
    ResultSetMetaData metaData = resultSet.getMetaData();

    int columnCount = metaData.getColumnCount();

    while (resultSet.next()) {
      List<String> row = new ArrayList<>();
      for (int i = 1; i <= columnCount; i++) {
        row.add(resultSet.getString(i));
      }
      result.add(row);
    }
    return result;
  }

  /**
   * This function inserts a row into a specified database table with the provided column names and
   * values.
   *
   * @param tableName
   * @param columnValues
   * @throws SQLException
   */
  public void insertIntoTable(String tableName, Map<String, Object> columnValues)
      throws SQLException {
    StringBuilder columns = new StringBuilder();
    StringBuilder values = new StringBuilder();

    for (Map.Entry<String, Object> entry : columnValues.entrySet()) {
      columns.append(entry.getKey()).append(", ");
      values.append("?, ");
    }

    // Remove the trailing comma and space
    columns.setLength(columns.length() - 2);
    values.setLength(values.length() - 2);

    String query =
        "INSERT INTO "
            + tableName
            + " ("
            + columns.toString()
            + ") VALUES ("
            + values.toString()
            + ")";

    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      int parameterIndex = 1;

      for (Object value : columnValues.values()) {
        preparedStatement.setObject(parameterIndex++, value);
      }
      preparedStatement.executeUpdate();
    }
  }

  /**
   * This function updates records in a specified database table with the provided updated values
   * based on a given condition.
   *
   * @param tableName
   * @param updatedValues
   * @param condition
   * @throws SQLException
   */
  public void updateRecord(String tableName, Map<String, Object> updatedValues, String condition)
      throws SQLException {
    StringBuilder setClause = new StringBuilder();

    for (Map.Entry<String, Object> entry : updatedValues.entrySet()) {
      setClause.append(entry.getKey()).append(" = ?, ");
    }

    // Remove the trailing comma and space
    setClause.setLength(setClause.length() - 2);

    String query = "UPDATE " + tableName + " SET " + setClause.toString() + " WHERE " + condition;

    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      int parameterIndex = 1;

      for (Object value : updatedValues.values()) {
        preparedStatement.setObject(parameterIndex++, value);
      }
      preparedStatement.executeUpdate();
    }
  }

  /**
   * This function deletes records from a specified database table based on a given condition.
   *
   * @param tableName
   * @param condition
   * @throws SQLException
   */
  public void deleteRecord(String tableName, String condition) throws SQLException {
    String query = "DELETE FROM " + tableName + " WHERE " + condition;

    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
      preparedStatement.executeUpdate();
    }
  }

  /**
   * This function executes a SQL query and prints the result. and each element in the inner list
   * represents a column value.
   *
   * @param query
   * @return List of lists where each inner list represents a row of the query result
   */
  public List<List<Object>> executeAndPrintQuery(String query) {
    List<List<Object>> result = new ArrayList<>();

    try (PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery()) {

      // Get metadata to fetch column names and types
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnCount = metaData.getColumnCount();

      // Fetch column names
      List<Object> columnNames = new ArrayList<>();
      for (int i = 1; i <= columnCount; i++) {
        columnNames.add(metaData.getColumnName(i));
      }
      result.add(columnNames);

      // Fetch data
      while (resultSet.next()) {
        List<Object> row = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
          Object value = resultSet.getObject(i);
          row.add(value);
        }
        result.add(row);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return result;
  }

  /**
   * This function return the connection client
   *
   * @return
   */
  @Override
  public Object getClient() {
    return connection;
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
}
