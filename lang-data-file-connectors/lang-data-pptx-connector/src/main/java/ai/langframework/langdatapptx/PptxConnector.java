package ai.langframework.langdatapptx;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;
import java.io.File;
import java.util.List;

/** Microsoft PowerPoint Connector */
public class PptxConnector implements Connector {
  /**
   * Loads data from a PPTX file using a PptxConnectorClient.
   *
   * @param file the PPTX file to load data from
   * @return a list of documents containing the data from the PPTX file
   */
  @Override
  public List<Document> loadData(File file) {
    return new PptxConnectorClient().readData(file);
  }

  /**
   * Loads data from a PPTX file located at the specified link using a PptxConnectorClient.
   *
   * @param link the link to the PPTX file
   * @return a list of documents containing the data from the PPTX file
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> loadData(String link) throws LoaderException {
    return new PptxConnectorClient().readData(link);
  }
}
