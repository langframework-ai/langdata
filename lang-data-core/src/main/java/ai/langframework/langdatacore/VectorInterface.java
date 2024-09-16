package ai.langframework.langdatacore;

import java.util.List;

public interface VectorInterface {

    public enum EmbeddingClientType {
        COHERE,
        OPENAI
    }
    void addDocuments(List<Document> documents );

    List<Document> searchSimilarity(String query);
}
