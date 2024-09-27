package ai.langframework.langdatapdf;

import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/** PDF Connector Client */
public class PdfConnectorClient implements ConnectorClient {
  /**
   * Reads data from a PDF file and creates documents.
   *
   * @param file The file to read data from.
   * @return The list of created documents.
   */
  @Override
  public List<Document> readData(File file) {

    List<Document> documents = new ArrayList<>();

    int numberOfPages = 0;

    String text = "";
    try {
      // Load the PDF document
      PDDocument document = PDDocument.load(file);

      // Get the number of pages in the document
      numberOfPages = document.getNumberOfPages();

      PDFTextStripper pdfStripper = new PDFTextStripper();

      text = pdfStripper.getText(document);

      // Close the document when you're done
      document.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    HashMap<String, String> metadata = new HashMap<>();
    metadata.put("FileName", file.getName());
    metadata.put("Source", file.getAbsolutePath());
    metadata.put("FileSize", String.valueOf(file.length()));
    metadata.put("NumberOfPages", String.valueOf(numberOfPages));

    documents.add(new Document(text, metadata));

    return documents;
  }

  /**
   * Reads data from a PDF link and creates documents.
   *
   * @param link The link to read data from.
   * @return The list of created documents.
   */
  @Override
  public List<Document> readData(String link) {

    List<Document> documents = new ArrayList<>();

    String text = "";
    URL url = null;
    int numberOfPages = 0;
    try {
      url = new URL(link);
      InputStream inputStream = url.openStream();
      PDDocument doc = PDDocument.load(inputStream);
      numberOfPages = doc.getNumberOfPages();
      PDFTextStripper pdfStripper = new PDFTextStripper();
      text = pdfStripper.getText(doc);
      doc.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    HashMap<String, String> metadata = new HashMap<>();
    metadata.put("FileName", url.getFile());
    metadata.put("Source", link);
    metadata.put("FileSize", String.valueOf(url.getFile().length()));
    metadata.put("NumberOfPages", String.valueOf(numberOfPages));

    documents.add(new Document(text, metadata));

    return documents;
  }

  /**
   * Gets the client object.
   *
   * @return The client object.
   */
  @Override
  public Object getClient() {
    return this;
  }
}
