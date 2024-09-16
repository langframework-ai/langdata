package ai.langframework.langdatacore;

/**
 * Constants for file URLs used throughout test cases.
 */
public final class PathFileConstants {

    // Prevent instantiation of the constants class
    private PathFileConstants() {}
    private static String hostedFileDomain = "https://shared-public-resources-free-ai.s3.amazonaws.com/";

    // CSV file URL
    public static final String CSV_FILE_URL = hostedFileDomain + "test.csv";

    // HTML file URL
    public static final String HTML_FILE_URL = hostedFileDomain + "test.html";

    // PDF file URL
    public static final String PDF_FILE_URL = hostedFileDomain + "test.pdf";

    // Text file URL
    public static final String TEXT_FILE_URL = hostedFileDomain + "test.txt";

    // Markdown file URL
    public static final String MD_FILE_URL = hostedFileDomain + "test.md";
    // Word Document file URL
    public static final String DOCX_FILE_URL = hostedFileDomain + "test.docx";
    // Word Document Template file URL
    public static final String DOTX_FILE_URL = hostedFileDomain + "test.dotx";
    // Powerpoint file URL
    public static final String PPTX_FILE_URL = hostedFileDomain + "test.pptx";
    // Json file URL
    public static final String JSON_FILE_URL = hostedFileDomain + "test.json";
    
    // PNG file URL
    public static final String PNG_FILE_URL = hostedFileDomain + "test.png";

    // Github file URL
    public static final String GITHUB_FILE_URL ="https://github.com/mit-nlp/MITIE/blob/master/tools/wordrep/src/word_vects.cpp";
    
    // Github issues URL
    public static final String GITHUB_ISSUE_URL ="https://github.com/lexfridman/mit-deep-learning/issues";

}
