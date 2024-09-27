package ai.langframework.langdatahtml;

import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jsoup.Jsoup;

/** HTML Connector Client */
public class HtmlConnectorClient implements ConnectorClient {

  /**
   * Reads data from an HTML file and creates documents.
   *
   * @param file The file to read data from.
   * @return The list of created documents.
   */
  @Override
  public List<Document> readData(File file) {
    List<Document> documents = new ArrayList<>();

    String text = "";
    String title = "";
    try {
      org.jsoup.nodes.Document doc = Jsoup.parse(file, "UTF-8");
      text = doc.body().text();
      title = doc.title();
    } catch (Exception e) {
      e.printStackTrace();
    }
    HashMap<String, String> metadata = new HashMap<>();
    metadata.put("Source", file.getAbsolutePath());
    metadata.put("Title", title);

    documents.add(new Document(text, metadata));
    return documents;
  }

  /**
   * Reads data from an HTML link and creates documents.
   *
   * @param link The link to read data from.
   * @return The list of created documents.
   */
  @Override
  public List<Document> readData(String link) {
    List<Document> documents = new ArrayList<>();

    String text = "";
    String title = "";
    try {
      org.jsoup.nodes.Document doc = Jsoup.connect(link).get();
      text = doc.body().text();
      title = doc.title();
    } catch (Exception e) {
      e.printStackTrace();
    }
    HashMap<String, String> metadata = new HashMap<>();
    metadata.put("Source", link);
    metadata.put("Title", title);

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
