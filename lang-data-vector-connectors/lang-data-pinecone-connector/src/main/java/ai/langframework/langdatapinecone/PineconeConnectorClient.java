package ai.langframework.langdatapinecone;

import ai.langframework.langdatacohereai.CohereAIConnectorClient;
import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.VectorInterface;
import ai.langframework.langdatacore.exceptions.LoaderException;
import ai.langframework.langdataopenai.OpenAIConnectorClient;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import io.pinecone.PineconeClientConfig;
import io.pinecone.PineconeConnection;
import io.pinecone.PineconeConnectionConfig;
import io.pinecone.proto.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.text.RandomStringGenerator;

/** Pinecone Connector Client */
public class PineconeConnectorClient implements ConnectorClient, VectorInterface {

  private PineconeConnection connection;
  private String namespace = "default";
  private int topK = 5;
  private String embeddingModel = "text-embedding-ada-002";
  private EmbeddingClientType embeddingClientType;
  private OpenAIConnectorClient modelOpenAIConnectorClient;
  private CohereAIConnectorClient modelCohereConnectorClient;

  /** The default constructor for Pinecone Connector Client */
  public PineconeConnectorClient() {
    connection = null;
  }

  /**
   * Initializes a new Pinecone connector client with the specified apikey, environment, project
   * name, index, and connection URL.
   *
   * @param apiKey The API key for authentication.
   * @param environment The environment for the Pinecone connector client.
   * @param projectName The project name for the Pinecone connector client.
   * @param index The index for the Pinecone connector client.
   * @param connectionUrl The connection URL for the Pinecone connector client.
   */
  public PineconeConnectorClient(
      String apiKey, String environment, String projectName, String index, String connectionUrl) {
    try {
      connection =
          new PineconeConnection(
              new PineconeClientConfig()
                  .withApiKey(apiKey)
                  .withEnvironment(environment)
                  .withProjectName(projectName),
              new PineconeConnectionConfig().withIndexName(index).withConnectionUrl(connectionUrl));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes a new Pinecone connector client with the specified API key, environment, project
   * name, index, connection URL, type of embedding client, and embedding model API key.
   *
   * @param apiKey The API key for authentication.
   * @param environment The environment for the Pinecone connector client.
   * @param projectName The project name for the Pinecone connector client.
   * @param index The index for the Pinecone connector client.
   * @param connectionUrl The connection URL for the Pinecone connector client.
   * @param embeddingClientType The type of embedding client.
   * @param modelApiKey The API key for the embedding model.
   */
  public PineconeConnectorClient(
      String apiKey,
      String environment,
      String projectName,
      String index,
      String connectionUrl,
      EmbeddingClientType embeddingClientType,
      String modelApiKey) {
    try {
      connection =
          new PineconeConnection(
              new PineconeClientConfig()
                  .withApiKey(apiKey)
                  .withEnvironment(environment)
                  .withProjectName(projectName),
              new PineconeConnectionConfig().withIndexName(index).withConnectionUrl(connectionUrl));

      this.embeddingClientType = embeddingClientType;

      switch (embeddingClientType) {
        case OPENAI:
          modelOpenAIConnectorClient = new OpenAIConnectorClient();
          modelOpenAIConnectorClient.initializeClient(modelApiKey);
          break;
        case COHERE:
          modelCohereConnectorClient = new CohereAIConnectorClient();
          modelCohereConnectorClient.initializeClient(modelApiKey);
          break;
        default:
          throw new Exception("Unsupported Embedding Client Type");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets the namespace for the Pinecone connector client.
   *
   * @param namespace The namespace to set.
   */
  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  /**
   * Sets the embedding model for the Pinecone connector client.
   *
   * @param embeddingModel The embedding model to set.
   */
  public void setEmbeddingModel(String embeddingModel) {
    this.embeddingModel = embeddingModel;
  }

  /**
   * Sets the top K value for the Pinecone connector client.
   *
   * @param topK The top K value to set.
   */
  public void setTopK(int topK) {
    this.topK = topK;
  }

  /**
   * Upserts the list of vectors and its corresponding text into the Pinecone vector store.
   *
   * @param vectors The list of vectors of the text.
   * @param text The text associated with the vectors.
   */
  public void upsertVectors(List<Float> vectors, String text) {
    try {
      Vector v1 =
          Vector.newBuilder()
              .setId(
                  new RandomStringGenerator.Builder()
                      .withinRange('0', 'z')
                      .filteredBy(Character::isLetterOrDigit)
                      .build()
                      .generate(3))
              .addAllValues(vectors)
              .setMetadata(
                  Struct.newBuilder()
                      .putFields("Text", Value.newBuilder().setStringValue(text).build())
                      .build())
              .build();

      UpsertRequest upsertRequest =
          UpsertRequest.newBuilder().addVectors(v1).setNamespace(namespace).build();

      UpsertResponse upsertResponse = connection.getBlockingStub().upsert(upsertRequest);

      if (upsertResponse.getUpsertedCount() == 0) {
        throw new Exception("Failed to upsert vectors");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Searches for similar records for the passed vectors.
   *
   * @param vectors The query vectors.
   * @return List of matching records.
   */
  public List<String> query(List<Float> vectors) {
    QueryRequest queryRequest =
        QueryRequest.newBuilder()
            .addAllVector(vectors)
            .setTopK(topK)
            .setNamespace(namespace)
            .setIncludeMetadata(true)
            .build();

    QueryResponse queryResponse = connection.getBlockingStub().query(queryRequest);

    Map<String, Value> fields;
    List<String> response = new ArrayList<>();

    for (ScoredVector scoredVector : queryResponse.getMatchesList()) {
      fields = scoredVector.getMetadata().getFieldsMap();
      if (!fields.isEmpty()) {
        Value value = fields.get("Text");
        response.add(value.getStringValue());
      }
    }
    return response;
  }

  /**
   * Returns the Pinecone connection object.
   *
   * @return The Pinecone connection object.
   */
  @Override
  public Object getClient() {
    return connection;
  }

  /**
   * This functionality is not available for this client.
   *
   * @param file The file from which to read the data.
   * @return A list of documents read from the file.
   * @throws LoaderException if an error occurs while loading the data.
   */
  @Override
  public List<Document> readData(File file) throws LoaderException {
    return null;
  }

  /**
   * This functionality is not available for this client.
   *
   * @param link The link from which to read the data.
   * @return A list of documents read from the link.
   * @throws LoaderException if an error occurs while loading the data.
   */
  @Override
  public List<Document> readData(String link) throws LoaderException {
    return null;
  }

  /**
   * Accepts a list of documents, convert it onto vectors and stores it into Database
   *
   * @param documents
   */
  @Override
  public void addDocuments(List<Document> documents) {
    try {
      if (embeddingClientType == EmbeddingClientType.OPENAI) {
        for (Document doc : documents) {
          List<Float[]> data =
              modelOpenAIConnectorClient.embedding(List.of(doc.getText()), embeddingModel);
          List<Float> floatList = new ArrayList<>();
          for (Float[] array : data) {
            for (Float value : array) {
              floatList.add(value);
            }
          }
          upsertVectors(floatList, doc.getText());
        }
      } else if (embeddingClientType == EmbeddingClientType.COHERE) {
        for (Document doc : documents) {
          Float[] data = modelCohereConnectorClient.embedText(doc.getText());
          List<Float> floatList = new ArrayList<>();
          for (Float value : data) {
            floatList.add(value);
          }
          upsertVectors(floatList, doc.getText());
        }
      } else {
        throw new Exception("Unsupported Embedding Client Type");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns a list of documents that are similar to the query passed
   *
   * @param query
   * @return
   */
  @Override
  public List<Document> searchSimilarity(String query) {
    List<Document> documents = new ArrayList<>();
    try {
      if (embeddingClientType == EmbeddingClientType.OPENAI) {

        List<Float[]> data = modelOpenAIConnectorClient.embedding(List.of(query), embeddingModel);
        List<Float> floatList = new ArrayList<>();
        for (Float[] array : data) {
          for (Float value : array) {
            floatList.add(value);
          }
        }
        List<String> response = query(floatList);
        for (String text : response) {
          documents.add(new Document(text));
        }
        return documents;

      } else if (embeddingClientType == EmbeddingClientType.COHERE) {

        Float[] data = modelCohereConnectorClient.embedText(query);

        List<Float> floatList = new ArrayList<>();
        for (Float value : data) {
          floatList.add(value);
        }

        List<String> response = query(floatList);

        for (String text : response) {
          documents.add(new Document(text));
        }
        return documents;

      } else {
        throw new Exception("Unsupported Embedding Client Type");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
