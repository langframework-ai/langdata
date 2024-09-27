package ai.langframework.langdatagithub;

import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/** GitHub Connector Client */
public class GithubConnectorClient implements ConnectorClient {

  /**
   * Retrieves the connector instance itself.
   *
   * @return the connector instance
   */
  @Override
  public Object getClient() {
    return null;
  }

  /**
   * This functionality is not available for this client.
   *
   * @param file the file to read data from
   * @return a list of documents
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> readData(File file) throws LoaderException {
    return null;
  }

  /**
   * Reads data from a link.
   *
   * @param link the link to read data from
   * @return a list of documents
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> readData(String link) throws LoaderException {
    List<Document> documents = new ArrayList<>();

    URL url = null;
    String repoContent = null;
    try {
      url = new URL(link);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (link.endsWith("issues")) {
      repoContent = getRepoIssues(link);
    } else {
      repoContent = getRepoContent(link);
    }

    HashMap<String, String> metadata = new HashMap<>();
    metadata.put("Source", link);
    metadata.put("FileName", url.getFile());
    metadata.put("FileSize", String.valueOf(url.getFile().length()));

    documents.add(new Document(repoContent, metadata));
    return documents;
  }

  /**
   * Retrieves the content of a repository from the given link.
   *
   * @param link the link to the repository
   * @return the content of the repository as a string
   */
  private String getRepoContent(String link) {
    org.jsoup.nodes.Document doc = null;
    try {
      doc = Jsoup.connect(link).get();
    } catch (Exception e) {
      e.printStackTrace();
    }
    Element repoContent = doc.getElementById("repo-content-pjax-container");
    Element reactApp = repoContent.child(0);
    Element content = reactApp.child(0);
    JSONObject jsonArray = new JSONObject(content.html());
    String payload = jsonArray.get("payload").toString();
    JSONObject payloadObj = new JSONObject(payload);
    String blob = payloadObj.get("blob").toString();
    JSONObject blobObj = new JSONObject(blob);
    Object rawLines = blobObj.get("rawLines");
    JSONArray rawLinesArray = new JSONArray(rawLines.toString());
    StringBuilder sb = new StringBuilder();
    rawLinesArray.forEach(line -> sb.append(line).append("\n"));
    return sb.toString();
  }

  /**
   * Retrieves the issues from a repository from the given link.
   *
   * @param link the link to the repository
   * @return the issues of the repository as a string
   */
  private String getRepoIssues(String link) {

    org.jsoup.nodes.Document doc = null;
    try {
      doc = Jsoup.connect(link).get();
    } catch (Exception e) {
      e.printStackTrace();
    }
    Element repoContent = doc.getElementById("repo-content-pjax-container");
    Elements tagAElements = repoContent.getElementsByTag("a");
    List<Element> issureList = new ArrayList<>();
    tagAElements.stream()
        .forEach(
            a -> {
              String aId = a.attr("id");
              if (StringUtils.isNotBlank(aId) && aId.startsWith("issue_")) {
                issureList.add(a);
              }
            });
    StringBuffer stringBuffer = new StringBuffer();
    issureList.stream()
        .forEach(
            a -> {
              stringBuffer.append(a.html() + "\n");
            });

    return stringBuffer.toString();
  }
}
