package ai.langframework.langdatawikipedia;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;
import java.io.File;
import java.util.List;

/** Wikipedia Connector */
public class WikipediaConnector implements Connector {
  ConnectorClient WeaviateConnectorClient;

  /**
   * Loads data using the WikipediaConnectorClient from a file.
   *
   * @param file the file to load data from
   * @return a list of documents containing the data from the file
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(File file) throws LoaderException {
    return new WikipediaConnectorClient().readData(file);
  }

  /**
   * Loads data using the WikipediaConnectorClient from a link.
   *
   * @param link the link to load data from
   * @return a list of documents containing the data from the link
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(String link) throws LoaderException {
    return new WikipediaConnectorClient().readData(link);
  }
}
