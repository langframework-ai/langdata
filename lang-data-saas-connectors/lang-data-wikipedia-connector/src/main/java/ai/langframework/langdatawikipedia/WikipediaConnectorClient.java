package ai.langframework.langdatawikipedia;

import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.Logger;
import ai.langframework.langdatacore.exceptions.LoaderException;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;

/** Wikipedia Article Loader */
public class WikipediaConnectorClient implements ConnectorClient {

  /**
   * Reads data corresponding to the given Wikipedia article title.
   *
   * @param articleTitle the title of the Wikipedia article
   * @return a list containing a single document representing the article content
   */
  @Override
  public List<Document> readData(String articleTitle) {
    List<Document> documents = new ArrayList<>();

    String content = "";
    try {
      content = getWikipediaContent(articleTitle);
      content = content.replaceAll("\\<.*?\\>", "");

    } catch (Exception e) {
      e.printStackTrace();
    }

    HashMap<String, String> metadata = new HashMap<>();
    metadata.put("ArticleTitle", articleTitle);

    documents.add(new Document(content, metadata));

    return documents;
  }

  /**
   * Retrieves the content of a Wikipedia article using its title.
   *
   * @param articleTitle the title of the Wikipedia article
   * @return the content of the article as a string
   * @throws Exception if there is an issue retrieving the content
   */
  private String getWikipediaContent(String articleTitle) throws Exception {
    String apiUrl =
        "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&titles="
            + articleTitle.replace(" ", "_")
            + "&redirects=true&exintro=true";

    URL url = new URL(apiUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
      StringBuilder response = new StringBuilder();
      String line;

      while ((line = reader.readLine()) != null) {
        response.append(line);
      }

      // Extract the text content from the JSON response
      String jsonString = response.toString();

      String text = extractContent(jsonString);

      return text;

    } finally {
      connection.disconnect();
    }
  }

  /**
   * Extracts the text content from the JSON response obtained from Wikipedia API.
   *
   * @param jsonString the JSON response from the Wikipedia API
   * @return the text content extracted from the JSON response
   * @throws Exception if there is an issue extracting the content
   */
  private static String extractContent(String jsonString) throws Exception {
    JSONObject jsonObject = new JSONObject(jsonString);

    try {
      // Navigate through the JSON structure to get to the "extract" field
      JSONObject query = jsonObject.getJSONObject("query");
      JSONObject pages = query.getJSONObject("pages");
      JSONObject page = pages.getJSONObject(pages.keys().next());
      String text = page.getString("extract");
      return text;
    } catch (Exception e) {
      Logger.info("Wikipedia Article not Found!");
    }

    return null;
  }

  /**
   * Retrieves the connector instance itself.
   *
   * @return the connector instance
   */
  @Override
  public Object getClient() {
    return this;
  }

  /**
   * This functionality is not available for this client.
   *
   * @param file the file to read data from
   * @return a list of documents (currently null)
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> readData(File file) throws LoaderException {
    return null;
  }
}
