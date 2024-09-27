package ai.langframework.langdatapdf;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.Document;
import java.io.File;
import java.util.List;

/** PDF Connector */
public class PdfConnector implements Connector {

  /**
   * Loads data from a PDF file using a PdfConnectorClient.
   *
   * @param file the PDF file to load data from
   * @return a list of documents containing the data from the PDF file
   */
  @Override
  public List<Document> loadData(File file) {
    return new PdfConnectorClient().readData(file);
  }

  /**
   * Loads data from a PDF file located at the specified link using a PdfConnectorClient.
   *
   * @param link the link to the PDF file
   * @return a list of documents containing the data from the PDF file
   */
  @Override
  public List<Document> loadData(String link) {
    return new PdfConnectorClient().readData(link);
  }
}
