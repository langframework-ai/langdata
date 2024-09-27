package ai.langframework.langdatacore.splitters;

import java.util.List;

public class RecursiveCharacterTextSplitter extends TextSplitter {

  /**
   * Constructor to initialize the Splitter
   *
   * @param chunkSize
   * @param chunkOverlap
   */
  public RecursiveCharacterTextSplitter(int chunkSize, int chunkOverlap) {
    super(chunkSize, chunkOverlap);
  }

  @Override
  List<String> splitText(String text) {
    return null;
  }
}
