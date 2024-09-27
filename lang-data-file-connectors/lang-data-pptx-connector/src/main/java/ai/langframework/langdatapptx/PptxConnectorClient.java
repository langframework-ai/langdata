package ai.langframework.langdatapptx;

import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;

/** Microsoft PowerPoint Connector Client */
public class PptxConnectorClient implements ConnectorClient {
  /**
   * Reads data from a PowerPoint file (.pptx) and creates documents.
   *
   * @param file The file to read data from.
   * @return The list of created documents.
   */
  @Override
  public List<Document> readData(File file) {
    List<Document> documents = new ArrayList<>();

    String text = "";

    // Read .pptx file
    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream(file);

      text = extractTextFromPptx(inputStream);
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
   * Reads data from a PowerPoint file (.pptx) located at the given link and creates documents.
   *
   * @param link The link to read data from.
   * @return The list of created documents.
   */
  @Override
  public List<Document> readData(String link) {
    List<Document> documents = new ArrayList<>();
    URL url = null;

    String text = "";

    // Load the PowerPoint presentation
    InputStream inputStream = null;

    try {
      url = new URL(link);
      inputStream = url.openStream();
      text = extractTextFromPptx(inputStream);
      inputStream.close();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    HashMap<String, String> metadata = new HashMap<>();
    metadata.put("Source", link);
    metadata.put("FileName", url.getFile());
    metadata.put("FileSize", String.valueOf(url.getFile().length()));

    documents.add(new Document(text, metadata));
    return documents;
  }

  /**
   * Extracts text from a PowerPoint file (.pptx) represented by the given input stream.
   *
   * @param inputStream The input stream representing the PowerPoint file.
   * @return The extracted text from the PowerPoint file.
   */
  private String extractTextFromPptx(InputStream inputStream) {
    String text = null;

    XMLSlideShow xmlA = null;
    try {
      xmlA = new XMLSlideShow(inputStream);

      SlideShowExtractor<XSLFShape, XSLFTextParagraph> ex = new SlideShowExtractor<>(xmlA);

      // Iterate over each slide
      int slideNumber = 0;
      for (XSLFSlide slide : xmlA.getSlides()) {
        slideNumber++;
        String slideText = ex.getText(slide);
        text += "Slide #" + slideNumber + ":\n" + slideText + "\n";
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (text != null) {
      return text;
    }
    return "null";
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
