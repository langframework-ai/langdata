package ai.langframework.langdatadocx;

import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

/** Word Document Connector Client */
public class DocxConnectorClient implements ConnectorClient {

  /**
   * Extracts text from a DOCX file.
   *
   * @param inputStream The input stream of the DOCX file.
   * @return The extracted text.
   */
  private String ExtractTextFromDocx(InputStream inputStream) {

    String text = "";

    try {
      XWPFDocument wordDocument = new XWPFDocument(inputStream);

      for (XWPFParagraph paragraph : wordDocument.getParagraphs()) {
        text = text + paragraph.getText();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return text != null ? text : "null";
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

  /**
   * Reads data from a DOCX file and creates documents.
   *
   * @param file The file to read data from.
   * @return The list of created documents.
   * @throws LoaderException If an error occurs during loading.
   */
  @Override
  public List<Document> readData(File file) throws LoaderException {

    List<Document> documents = new ArrayList<>();

    String text = "";

    // Open the Word document using FileInputStream
    FileInputStream inputStream = null;

    try {
      inputStream = new FileInputStream(file);

      text = ExtractTextFromDocx(inputStream);

      inputStream.close();

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    HashMap<String, String> metadata = new HashMap<>();
    metadata.put("FileName", file.getName());
    metadata.put("Source", file.getAbsolutePath());
    metadata.put("FileSize", String.valueOf(file.length()));

    documents.add(new Document(text, metadata));

    return documents;
  }

  /**
   * Reads data from a DOCX link and creates documents.
   *
   * @param link The link to read data from.
   * @return The list of created documents.
   * @throws LoaderException If an error occurs during loading.
   */
  @Override
  public List<Document> readData(String link) throws LoaderException {

    List<Document> documents = new ArrayList<>();

    String text = "";
    URL url = null;

    try {
      url = new URL(link);

      // Open the Word document using InputStream
      InputStream inputStream = url.openStream();

      text = ExtractTextFromDocx(inputStream);

      inputStream.close();

    } catch (Exception e) {
      e.printStackTrace();
    }

    HashMap<String, String> metadata = new HashMap<>();
    metadata.put("Source", link);
    metadata.put("FileName", url.getFile());
    metadata.put("FileSize", String.valueOf(url.getFile().length()));

    documents.add(new Document(text, metadata));

    return documents;
  }
}
