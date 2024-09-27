package ai.langframework.langdatajson;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;
import java.io.File;
import java.util.List;

/** JSON Connector */
public class JsonConnector implements Connector {

  /**
   * Loads data from a JSON file using a JsonConnectorClient.
   *
   * @param file the JSON file to load data from
   * @return a list of documents containing the data from the JSON file
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(File file) throws LoaderException {
    return new JsonConnectorClient().readData(file);
  }

  /**
   * Loads data from a JSON file located at the specified link using a JsonConnectorClient.
   *
   * @param link the link to the JSON file
   * @return a list of documents containing the data from the JSON file
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(String link) throws LoaderException {
    return new JsonConnectorClient().readData(link);
  }
}
