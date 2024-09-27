package ai.langframework.langdatacore.splitters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** CharacterTextSplitter */
public class CharacterTextSplitter extends TextSplitter {
  private String separator;
  private boolean isSeparatorRegex;

  /**
   * Constructor to initialize the Splitter
   *
   * @param separator
   * @param chunkSize
   * @param chunkOverlap
   * @param isSeparatorRegex
   */
  public CharacterTextSplitter(
      String separator, int chunkSize, int chunkOverlap, boolean isSeparatorRegex) {
    super(chunkSize, chunkOverlap);
    this.separator = separator;
    this.isSeparatorRegex = isSeparatorRegex;
  }

  /**
   * Constructor to initialize the Splitter
   *
   * @param chunkSize
   * @param chunkOverlap
   */
  public CharacterTextSplitter(int chunkSize, int chunkOverlap) {
    super(chunkSize, chunkOverlap);
    this.separator = "\n\n";
    this.isSeparatorRegex = false;
  }

  /**
   * Method that splits the text passed into chunks
   *
   * @param text
   * @return List<String>
   */
  @Override
  List<String> splitText(String text) {
    validateParameters(text);

    List<String> result = new ArrayList<>();

    if (isSeparatorRegex) {
      return Arrays.asList(text.split(separator));
    }

    int length = text.length();
    int startIndex = 0;

    while (startIndex < length) {
      int endIndex = startIndex + chunkSize;

      // Adjust endIndex based on chunkOverlap
      endIndex = Math.min(endIndex, length);

      // Find the actual end index by looking for the next occurrence of the separator
      int separatorIndex = text.indexOf(separator, endIndex);
      if (separatorIndex != -1) {
        endIndex = separatorIndex + separator.length();
      }

      String chunk = text.substring(startIndex, endIndex);
      result.add(chunk.replace(separator, ""));

      // Move startIndex based on chunkOverlap
      if (endIndex < length) {
        startIndex = endIndex - chunkOverlap;
      } else {
        startIndex = endIndex;
      }
    }
    return result;
  }

  /**
   * Method to validate the parameters
   *
   * @param text
   */
  @Override
  protected void validateParameters(String text) {
    super.validateParameters(text);
    if (separator == null) {
      throw new IllegalArgumentException("Separator cannot be null");
    }
  }
}
