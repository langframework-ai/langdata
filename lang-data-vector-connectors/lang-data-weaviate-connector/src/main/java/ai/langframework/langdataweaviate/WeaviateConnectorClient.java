package ai.langframework.langdataweaviate;

import ai.langframework.langdatacohereai.CohereAIConnectorClient;
import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.Logger;
import ai.langframework.langdatacore.VectorInterface;
import ai.langframework.langdatacore.exceptions.LoaderException;
import ai.langframework.langdataopenai.OpenAIConnectorClient;
import io.weaviate.client.Config;
import io.weaviate.client.WeaviateAuthClient;
import io.weaviate.client.WeaviateClient;
import io.weaviate.client.base.Result;
import io.weaviate.client.v1.data.model.WeaviateObject;
import io.weaviate.client.v1.graphql.model.GraphQLResponse;
import io.weaviate.client.v1.graphql.query.argument.NearTextArgument;
import io.weaviate.client.v1.graphql.query.argument.NearVectorArgument;
import io.weaviate.client.v1.graphql.query.fields.Field;
import io.weaviate.client.v1.schema.model.Property;
import io.weaviate.client.v1.schema.model.Schema;
import io.weaviate.client.v1.schema.model.WeaviateClass;
import java.io.File;
import java.util.*;

/** Weaviate Connector Client */
public class WeaviateConnectorClient implements ConnectorClient, VectorInterface {
  private WeaviateClient client;
  private String URL;
  private String apiKey;
  private String className;
  private EmbeddingClientType embeddingClientType;
  private OpenAIConnectorClient modelOpenAIConnectorClient;
  private CohereAIConnectorClient modelCohereConnectorClient;
  private String embeddingModel = "text-embedding-ada-002";

  /** Initializes a new Weaviate connector client with no specific configuration. */
  public WeaviateConnectorClient() {
    client = null;
  }

  /**
   * Constructor to initialize the client
   *
   * @param URL
   * @param apiKey
   */
  public WeaviateConnectorClient(String URL, String apiKey) {
    try {
      Config config = new Config("https", URL);
      client = WeaviateAuthClient.apiKey(config, apiKey);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes a new Weaviate connector client with the specified URL, API key, embedding client
   * type, and model API key.
   *
   * @param URL The URL of the Weaviate server.
   * @param apiKey The API key for authentication with the Weaviate server.
   * @param embeddingClientType The type of embedding client to use.
   * @param modelApiKey The API key required for accessing the embedding model (e.g., OpenAI or
   *     Cohere).
   */
  public WeaviateConnectorClient(
      String URL, String apiKey, EmbeddingClientType embeddingClientType, String modelApiKey) {
    try {
      Config config = new Config("https", URL);
      client = WeaviateAuthClient.apiKey(config, apiKey);

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
   * Constructor to initialize the client (in case of using Weaviate's vectorizer)
   *
   * @param URL
   * @param apiKey
   * @param openAIKey
   */
  public WeaviateConnectorClient(String URL, String apiKey, String openAIKey) {
    Map<String, String> headers =
        new HashMap<String, String>() {
          {
            put("X-OpenAI-Api-Key", openAIKey);
          }
        };
    try {
      Config config = new Config("https", URL, headers);
      client = WeaviateAuthClient.apiKey(config, apiKey);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets the embedding model
   *
   * @param embeddingModel
   */
  public void setEmbeddingModel(String embeddingModel) {
    this.embeddingModel = embeddingModel;
  }

  /**
   * Sets the class name for the connector client.
   *
   * @param className The class name to be set.
   */
  public void setClassName(String className) {
    this.className = className;
  }

  /**
   * This method is used to get the schema of the Weaviate Vector Store
   *
   * @return Schema in String format
   */
  public String getSchema() {
    Result<Schema> result = null;
    try {
      result = client.schema().getter().run();
      if (result.hasErrors()) {
        Logger.info(result.getResult());
        Logger.info(result.getError());
        throw new Exception("Error while getting schema");
      } else {
        return result.getResult().toString();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result.getResult().toString();
  }

  /**
   * Method used to create a class in Weaviate Vector Store with a vectorizer
   *
   * @param className
   * @param classDescription
   * @param vectorizer
   */
  public void createClass(String className, String classDescription, String vectorizer) {
    WeaviateClass weaviateClass = null;
    try {
      weaviateClass =
          WeaviateClass.builder()
              .className(className)
              .description(classDescription)
              .vectorizer(vectorizer)
              .build();

      if (weaviateClass != null) {
        Result<Boolean> result = client.schema().classCreator().withClass(weaviateClass).run();
        if (result.hasErrors()) {
          throw new Exception("Unable to create Weaviate Class");
        }
      } else {
        throw new Exception("Unable to create Weaviate Class");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Method used to create a class in Weaviate Vector Store without a vectorizer
   *
   * @param className
   * @param classDescription
   */
  public void createClass(String className, String classDescription) {
    WeaviateClass weaviateClass = null;
    String vectorizer = "none";
    try {
      weaviateClass =
          WeaviateClass.builder()
              .className(className)
              .description(classDescription)
              .vectorizer(vectorizer)
              .build();

      if (weaviateClass != null) {
        Result<Boolean> result = client.schema().classCreator().withClass(weaviateClass).run();
        if (result.hasErrors()) {
          throw new Exception("Unable to create Weaviate Class");
        }
      } else {
        throw new Exception("Unable to create Weaviate Class");
      }
    } catch (Exception e) {
      Logger.info("Unable to create Weaviate Class");
      e.printStackTrace();
    }
  }

  /**
   * Method to get the class from Weaviate Vector Store
   *
   * @param className
   * @return Class in String format
   */
  public String getClass(String className) {
    Result<WeaviateClass> result = null;
    try {
      result = client.schema().classGetter().withClassName(className).run();
      if (result.hasErrors()) {
        throw new Exception("Error while getting class");
      } else {
        return result.getResult().toString();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result.getResult().toString();
  }

  /**
   * Method used to delete a class in Weaviate Vector Store
   *
   * @param className
   */
  public void deleteClass(String className) {
    Result<Boolean> result = null;
    try {
      result = client.schema().classDeleter().withClassName(className).run();
      if (result.hasErrors()) {
        throw new Exception("Error while deleting class");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Method used to add Properties to a Weavite Class
   *
   * @param className
   * @param propertyName
   * @param type
   */
  public void addProperty(String className, String propertyName, String type) {
    Property property = null;
    try {
      property = Property.builder().dataType(Arrays.asList(type)).name(propertyName).build();
      if (property != null) {
        Result<Boolean> result =
            client.schema().propertyCreator().withClassName(className).withProperty(property).run();
        if (result.hasErrors()) {
          throw new Exception("Unable to create Weaviate Class Property");
        }
      } else {
        throw new Exception("Unable to create Weaviate Class Property");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Method used to add data object in Weaviate Class
   *
   * @param className
   * @param dataSchema
   *     <p>Sample dataSchema: Map<String, Object> dataSchema = new HashMap<>();
   *     dataSchema.put("name", "Jodi Kantor"); dataSchema.put("writesFor", new HashMap() { {
   *     put("beacon", "weaviate://localhost/f81bfe5e-16ba-4615-a516-46c2ae2e5a80"); } }
   * @param consistencyLevel
   */
  public void addDataObject(
      String className, Map<String, Object> dataSchema, String consistencyLevel) {
    try {
      Result<WeaviateObject> result =
          client
              .data()
              .creator()
              .withClassName(className)
              .withProperties(dataSchema)
              .withConsistencyLevel(consistencyLevel)
              .run();

      if (result.hasErrors()) {
        throw new Exception("Unable to add Weaviate Data Object");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Method used to add data object in Weaviate Class without consistencyLevel
   *
   * @param className
   * @param dataSchema
   */
  public void addDataObject(String className, Map<String, Object> dataSchema) {
    try {
      Result<WeaviateObject> result =
          client.data().creator().withClassName(className).withProperties(dataSchema).run();

      if (result.hasErrors()) {
        throw new Exception("Unable to add Weaviate Data Object");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Method used to add data object in Weaviate Class with vectors
   *
   * @param className
   * @param dataSchema
   * @param vector
   */
  public void addDataObjectWithVector(
      String className, Map<String, Object> dataSchema, Float[] vector) {
    try {
      Result<WeaviateObject> result =
          client
              .data()
              .creator()
              .withClassName(className)
              .withVector(vector)
              .withProperties(dataSchema)
              .run();

      if (result.hasErrors()) {
        System.out.println(result.getError());
        throw new Exception("Unable to add Weaviate Data Object with vector");
      }
    } catch (Exception e) {
      e.printStackTrace();
      e.getMessage();
    }
  }

  /**
   * Method to perform Similarity search in Weaviate Vector store
   *
   * @param text
   * @param className
   * @return Response Object
   */
  public Object search(String text, String className) {
    try {
      Field _additional =
          Field.builder()
              .name("_additional")
              .fields(
                  new Field[] {
                    Field.builder().name("vector").build(), // only supported if distance==cosine
                    Field.builder().name("distance").build(), // always supported
                  })
              .build();

      NearTextArgument nearText =
          client
              .graphQL()
              .arguments()
              .nearTextArgBuilder()
              .concepts(new String[] {text})
              .distance(0.6f) // use .certainty(0.7f) prior to v1.14
              .build();

      Result<GraphQLResponse> result =
          client
              .graphQL()
              .get()
              .withClassName(className)
              .withFields(_additional)
              .withNearText(nearText)
              .run();

      if (result.hasErrors()) {
        throw new Exception("Unable to fetch Weaviate Data Objects");
      } else {
        return result.getResult().getData();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Method to perform Similarity search in Weaviate Vector store and return vectors along with
   * fieldNames passed
   *
   * @param text
   * @param className
   * @param fieldName1
   * @return Response Object
   */
  public Object search(String text, String className, String fieldName1) {
    try {
      Field field1 = Field.builder().name(fieldName1).build();

      Field _additional =
          Field.builder()
              .name("_additional")
              .fields(
                  new Field[] {
                    Field.builder().name("vector").build(), // only supported if distance==cosine
                    Field.builder().name("distance").build(), // always supported
                  })
              .build();

      NearTextArgument nearText =
          client
              .graphQL()
              .arguments()
              .nearTextArgBuilder()
              .concepts(new String[] {text})
              .distance(0.6f) // use .certainty(0.7f) prior to v1.14
              .build();

      Result<GraphQLResponse> result =
          client
              .graphQL()
              .get()
              .withClassName(className)
              .withFields(field1, _additional)
              .withNearText(nearText)
              .run();

      if (result.hasErrors()) {
        throw new Exception("Unable to fetch Weaviate Data Objects");
      } else {
        return result.getResult().getData();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Method to perform Similarity search in Weaviate Vector store and return vectors along with
   * fieldNames passed
   *
   * @param text
   * @param className
   * @param fieldName1
   * @param fieldName2
   * @return Response Object
   */
  public Object search(String text, String className, String fieldName1, String fieldName2) {
    try {
      Field field1 = Field.builder().name(fieldName1).build();
      Field field2 = Field.builder().name(fieldName2).build();
      Field _additional =
          Field.builder()
              .name("_additional")
              .fields(
                  new Field[] {
                    Field.builder().name("vector").build(), // only supported if distance==cosine
                    Field.builder().name("distance").build(), // always supported
                  })
              .build();

      NearTextArgument nearText =
          client
              .graphQL()
              .arguments()
              .nearTextArgBuilder()
              .concepts(new String[] {text})
              .distance(0.6f) // use .certainty(0.7f) prior to v1.14
              .build();

      Result<GraphQLResponse> result =
          client
              .graphQL()
              .get()
              .withClassName(className)
              .withFields(field1, field2, _additional)
              .withNearText(nearText)
              .run();

      if (result.hasErrors()) {
        throw new Exception("Unable to fetch Weaviate Data Objects");
      } else {
        return result.getResult().getData();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Method to perform Similarity search in Weaviate Vector store and return vectors along with
   * fieldNames passed
   *
   * @param text
   * @param className
   * @param fieldName1
   * @param fieldName2
   * @param fieldName3
   * @return Response Object
   */
  public Object search(
      String text, String className, String fieldName1, String fieldName2, String fieldName3) {
    try {
      Field field1 = Field.builder().name(fieldName1).build();
      Field field2 = Field.builder().name(fieldName2).build();
      Field field3 = Field.builder().name(fieldName2).build();

      Field _additional =
          Field.builder()
              .name("_additional")
              .fields(
                  new Field[] {
                    Field.builder().name("vector").build(), // only supported if distance==cosine
                    Field.builder().name("distance").build(), // always supported
                  })
              .build();

      NearTextArgument nearText =
          client
              .graphQL()
              .arguments()
              .nearTextArgBuilder()
              .concepts(new String[] {text})
              .distance(0.6f) // use .certainty(0.7f) prior to v1.14
              .build();

      Result<GraphQLResponse> result =
          client
              .graphQL()
              .get()
              .withClassName(className)
              .withFields(field1, field2, field3, _additional)
              .withNearText(nearText)
              .run();

      if (result.hasErrors()) {
        throw new Exception("Unable to fetch Weaviate Data Objects");
      } else {
        return result.getResult().getData();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
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
   * Retrieves the client object associated with this connector.
   *
   * @return The client object.
   */
  @Override
  public Object getClient() {
    return client;
  }

  /**
   * Sets the URL for the connector.
   *
   * @param url The URL to be set.
   */
  public void setUrl(String url) {
    this.URL = url;
  }

  /**
   * Sets the API key for the connector.
   *
   * @param api The API key to be set.
   */
  public void setApi(String api) {
    this.apiKey = api;
  }

  /**
   * Initializes the client with the specified URL and API key.
   *
   * @param URL The URL to be set for the client.
   * @param apiKey The API key to be set for the client.
   */
  public void initializeClient(String URL, String apiKey) {
    try {
      Config config = new Config("https", URL);
      client = WeaviateAuthClient.apiKey(config, apiKey);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Accepts a list of documents, convert it onto vectors and stores it into Weaviate
   *
   * @param documents
   */
  @Override
  public void addDocuments(List<Document> documents) {
    try {
      if (className == null) {
        throw new Exception("Class Name is not set");
      }
      if (embeddingClientType == EmbeddingClientType.OPENAI) {
        for (Document doc : documents) {
          Map<String, Object> dataSchema = new HashMap<>();
          dataSchema.put("text", doc.getText());

          List<Float[]> data =
              modelOpenAIConnectorClient.embedding(List.of(doc.getText()), embeddingModel);

          addDataObjectWithVector(className, dataSchema, data.get(0));
        }

      } else if (embeddingClientType == EmbeddingClientType.COHERE) {
        for (Document doc : documents) {
          Map<String, Object> dataSchema = new HashMap<>();
          dataSchema.put("text", doc.getText());

          Float[] data = modelCohereConnectorClient.embedText(doc.getText());

          addDataObjectWithVector(className, dataSchema, data);
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
      if (className == null) {
        throw new Exception("Class Name is not set");
      }
      if (embeddingClientType == EmbeddingClientType.OPENAI) {

        List<Float[]> data = modelOpenAIConnectorClient.embedding(List.of(query), embeddingModel);

        GraphQLResponse response = searchWithVectors(data.get(0));

        documents.add(new Document(response.getData().toString()));
        return documents;

      } else if (embeddingClientType == EmbeddingClientType.COHERE) {

        Float[] data = modelCohereConnectorClient.embedText(query);
        GraphQLResponse response = searchWithVectors(data);
        documents.add(new Document(response.toString()));
        return documents;

      } else {
        throw new Exception("Unsupported Embedding Client Type");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Searches with the specified vectors.
   *
   * @param vectors The vectors to search with.
   * @return The GraphQL response from the search operation.
   */
  private GraphQLResponse searchWithVectors(Float[] vectors) {
    Field text = Field.builder().name("text").build();
    Field _additional =
        Field.builder()
            .name("_additional")
            .fields(
                new Field[] {
                  Field.builder().name("vector").build(), // only supported if distance==cosine
                  Field.builder().name("distance").build(), // always supported
                })
            .build();

    NearVectorArgument nearVector = NearVectorArgument.builder().vector(vectors).build();

    Result<GraphQLResponse> result =
        client
            .graphQL()
            .get()
            .withClassName(className)
            .withFields(text, _additional)
            .withNearVector(nearVector)
            .run();

    return result.getResult();
  }
}
