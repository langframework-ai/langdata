//import ai.langframework.langdatacore.Config;
//import ai.langframework.langdatasqldatabase.DatabaseSqlConnector;
//import ai.langframework.langdatasqldatabase.DatabaseSqlConnectorClient;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class DatabaseSqlConnectorTest {
//
//    private String driver = Config.getApiKey("SQL_DRIVER");
//
//    private String url = Config.getApiKey("SQL_URL");
//    private String username = Config.getApiKey("SQL_USERNAMEDB");
//    private String password = Config.getApiKey("SQL_PASSWORD");
//
//    DatabaseSqlConnector client;
//
//    DatabaseSqlConnectorClient connectorClient;
//
//    @BeforeAll
//    void initializeclient(){
//        client = new DatabaseSqlConnector();
//        client.setDriver(driver);
//        client.setUrl(url);
//        client.setUsername(username);
//        client.setPassword(password);
//        client.initializeClient();
//        connectorClient = client.getConnectorClient();
//    }
//
//    void getSourceClient() {
//        Connection sourceClient = client.getSourceClient();
//        assertNotNull(sourceClient);
//    }
//
//    @Test
//    @Order(1)
//    void testClient() {
//        assertNotNull(client);
//    }
//
//    @Test
//    @Order(2)
//    void getrows() throws SQLException
//    {
//        connectorClient.getTablesRows("test");
//    }
//    @Test
//    @Order(3)
//    void testGetTablesRows() throws SQLException {
//        // Call the getTablesRows method to retrieve rows from the 'test' table
//        List<List<String>> rows = connectorClient.getTablesRows("test");
//
//        // Assert that the returned rows list is not null
//        assertNotNull(rows, "Returned rows list should not be null");
//    }
//
//    @Test
//    void insertRows() throws SQLException
//    {
//        //      TO INSERT DATA INTO TABLES
//
//        Map<String, Object> dataToInsert = Map.of("name", "test", "age", 24);
//
//        try {
//            connectorClient.insertIntoTable("test", dataToInsert);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        connectorClient.getTablesRows("test");
//
//    }
//    @Test
//    @Order(4)
//    void insertRowsTest() throws SQLException {
//        // Data to be inserted
//        Map<String, Object> dataToInsert = Map.of("name", "test", "age", 22);
//        // Insert data into the "test" table
//        try {
//            connectorClient.insertIntoTable("test", dataToInsert);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        // Retrieve the rows from the "test" table
//        List<List<String>> rows = connectorClient.getTablesRows("test");
//
//        // Verify that the insertion was successful
//        assertFalse(rows.isEmpty(), "No rows retrieved from the 'test' table after insertion");
//
//        // Verify that the inserted data appears in the table
//        boolean dataInserted = false;
//        for (List<String> row : rows) {
//            String name = (String) row.get(0); // Assuming the first column is 'name'
//            String age = (String) row.get(1); // Assuming the second column is 'age'
//            if (name.equals("test") && age.equals("22")) {
//
//                dataInserted = true;
//                break;
//            }
//        }
//
//        assertTrue(dataInserted, "Inserted data ('name': 'test', 'age': 22) not found in the 'test' table");
//
//    }
//    @Test
//    @Order(5)
//    void updatedRowsTest() throws SQLException {
//        // Define the updated values
//        Map<String, Object> updatedValues = Map.of("age", 24);
//        // Define the condition for the update
//
//        String condition = "name = 'abc'";
//
//
//        try {
//            // Perform the update operation
//            connectorClient.updateRecord("test", updatedValues, condition);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            fail("Update operation failed: " + e.getMessage());
//        }
//
//        // Retrieve the updated rows
//        List<List<String>> updatedRows = connectorClient.getTablesRows("test");
//
//        // Assert that the updated rows exist and are correct
//        assertNotNull(updatedRows, "Updated rows should not be null");
//        assertFalse(updatedRows.isEmpty(), "Updated rows should not be empty");
//
//        // If the update was successful, the age of the row with name 'test' should be 24
//        boolean foundtest = false;
//        for (List<String> row : updatedRows) {
//            if (row.contains("abc")) {
//                foundtest = true;
//                assertEquals("24", row.get(1), "Age of 'abc' should be 24 after update");
//                break;
//            }
//        }
//        assertTrue(foundtest, "Row with name 'abc' not found after update");
//    }
//
//    @Test
//    void deleteRows() throws SQLException
//    {
//        //      TO TEST DELETE FUNCTION
//
//        String condition = "name = 'test'";
//
//        try {
//            connectorClient.deleteRecord("test", condition);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        connectorClient.getTablesRows("test");
//
//    }
//    @Test
//    @Order(6)
//    void testDeleteRows() throws SQLException {
//        // Define the delete condition
//        String condition = "name = 'test'";
//
//
//        // Call the deleteRecord method to delete rows from the "test" table
//        try {
//            connectorClient.deleteRecord("test", condition);
//        } catch (SQLException e) {
//            e.printStackTrace();
//            fail("Delete operation failed: " + e.getMessage());
//        }
//
//        // Retrieve the rows from the "test" table after the delete operation
//        List<List<String>> rowsAfterDelete;
//        try {
//            rowsAfterDelete = connectorClient.getTablesRows("test");
//        } catch (SQLException e) {
//            e.printStackTrace();
//            fail("Failed to retrieve rows after delete: " + e.getMessage());
//            return; // Terminate the test if row retrieval fails
//        }
//
//        // Assert that the rows have been deleted as expected
//        boolean foundDeletedRow = false;
//        for (List<String> row : rowsAfterDelete) {
//            // Check if the row meets the delete condition
//            if (row.get(0).equals("test")) { // Assuming "name" is the first column
//
//                foundDeletedRow = true;
//                break;
//            }
//        }
//
//        assertFalse(foundDeletedRow, "Deleted row still exists in the result");
//    }
//
//    @Test
//    void anyQuery() throws SQLException
//    {
//        //        TO execute any query uin database
//        String query = "CREATE TABLE test (name VARCHAR(255), age INT );";
//        connectorClient.executeAndPrintQuery(query);
//
//    }
//    @Test
//    @Order(7)
//    void testAnyQuery() throws SQLException {
//
//        // Retrieve the table metadata to verify that the table has been created successfully
//        List<List<Object>> tableMetadata;
//        tableMetadata = connectorClient.executeAndPrintQuery("SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'test';");
//
//        // Assert that the table exists and its structure matches the expected schema
//        assertNotNull(tableMetadata, "Table metadata should not be null");
//        assertEquals(3, tableMetadata.size(), "Table should have 3 columns");
//        assertEquals("name", tableMetadata.get(1).get(0), "First column should be 'name'");
//        assertEquals("age", tableMetadata.get(2).get(0), "Second column should be 'age'");
//    }
//
//}