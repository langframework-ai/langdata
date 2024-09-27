package ai.langframework.langdatacsv;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.Document;
import java.io.File;
import java.util.List;

/** CSV Connector */
public class CsvConnector implements Connector {

  /**
   * Loads data from a CSV file using a CsvConnectorClient.
   *
   * @param file the CSV file to load data from
   * @return a list of documents containing the data from the CSV file
   */
  @Override
  public List<Document> loadData(File file) {
    return new CsvConnectorClient().readData(file);
  }

  /**
   * Loads data from a CSV file located at the specified link using a CsvConnectorClient.
   *
   * @param link the link to the CSV file
   * @return a list of documents containing the data from the CSV file
   */
  @Override
  public List<Document> loadData(String link) {
    return new CsvConnectorClient().readData(link);
  }
}
