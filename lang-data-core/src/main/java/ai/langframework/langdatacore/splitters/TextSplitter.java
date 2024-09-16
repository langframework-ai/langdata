package ai.langframework.langdatacore.splitters;

import ai.langframework.langdatacore.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An Abstract TextSplitter with common methods and variables
 */
public abstract class TextSplitter {

    protected int chunkSize;

    protected int chunkOverlap;

    /**
     * Constructor to initialize the Splitter
     * @param chunkSize
     * @param chunkOverlap
     */
    public TextSplitter(int chunkSize, int chunkOverlap){
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    /**
     * Method to split each Document from the list of Documents passed
     * @param inputDocuments
     * @return List<Document>
     */
    public List<Document> createDocuments(List<Document> inputDocuments) {
        List<Document> outputDocuments = new ArrayList<>();

        for (Document inputDocument : inputDocuments) {
            List<String> splitChunks = splitText(inputDocument.getText());
            for (int i = 0; i < splitChunks.size(); i++) {
                String chunk = splitChunks.get(i);
                Map<String, String> metadata = new HashMap<>(inputDocument.getMetadata());
                metadata.put("lookup_index", String.valueOf(i));
                Document outputDocument = new Document(chunk, metadata);
                outputDocuments.add(outputDocument);
            }
        }

        return outputDocuments;
    }

    /**
     * Method to split the Document passed
     * @param inputDocument
     * @return List<Document>
     */
    public List<Document> createDocuments(Document inputDocument) {
        List<Document> outputDocuments = new ArrayList<>();
        List<String> splitChunks;

        splitChunks = splitText(inputDocument.getText());
        for (int i = 0; i < splitChunks.size(); i++) {
            String chunk = splitChunks.get(i);
            Map<String, String> metadata = new HashMap<>(inputDocument.getMetadata());
            metadata.put("lookup_index", String.valueOf(i));
            Document outputDocument = new Document(chunk, metadata);
            outputDocuments.add(outputDocument);
        }

        return outputDocuments;
    }

    /**
     * Method to validate the parameters
     * @param text
     */
    protected void validateParameters(String text){
        if (text == null ) {
            throw new IllegalArgumentException("Text cannot be null");
        }
        else if(chunkSize <= 0){
            throw new IllegalArgumentException("Chunk Size should be greater than 0");
        }
        else if(chunkOverlap < 0){
            throw new IllegalArgumentException("Chunk Overlap should be greater than or equal to 0");
        }
    }
    abstract List<String> splitText(String text);

}
