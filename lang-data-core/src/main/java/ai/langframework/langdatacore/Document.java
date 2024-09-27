package ai.langframework.langdatacore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Document {
  // Class Attributes
  private String text;
  private Map<String, String> metadata;

  /** Default constructor initializing with empty text and metadata. */
  public Document() {
    text = "";
    metadata = new HashMap<>();
  }

  /**
   * Constructor with text initialization.
   *
   * @param text The textual content of the document.
   */
  public Document(String text) {
    this.text = text != null ? text : "";
    metadata = new HashMap<>();
  }

  /**
   * Constructor with text and metadata initialization.
   *
   * @param text The textual content of the document.
   * @param metadata The metadata associated with the document.
   */
  public Document(String text, Map<String, String> metadata) {
    this.text = text != null ? text : "";
    this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
  }

  /**
   * Gets the text of the document.
   *
   * @return The text content.
   */
  public String getText() {
    return text;
  }

  /**
   * Gets the metadata of the document.
   *
   * @return An unmodifiable map of the metadata.
   */
  public Map<String, String> getMetadata() {
    // Return an unmodifiable view of the metadata map to preserve encapsulation
    return Collections.unmodifiableMap(metadata);
  }

  /**
   * Adds or updates a metadata entry.
   *
   * @param key The key for the metadata entry.
   * @param value The value for the metadata entry.
   */
  public void addMetadata(String key, String value) {
    if (key != null && value != null) {
      metadata.put(key, value);
    }
  }

  /**
   * Prints the document content to the console for debugging purposes. This method can be extended
   * or modified for different logging or output requirements.
   */
  public void printContent() {
    Logger.info("Text: ");
    Logger.info(text);
    Logger.info("MetaData:");
    metadata.forEach((key, value) -> Logger.info(key + ": " + value));
  }
}
