package ai.langframework.langdatacohereai;

import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;
import com.cohere.api.Cohere;
import com.cohere.api.core.ClientOptions;
import com.cohere.api.core.Environment;
import com.cohere.api.requests.ChatRequest;
import com.cohere.api.requests.ClassifyRequest;
import com.cohere.api.requests.EmbedRequest;
import com.cohere.api.requests.GenerateRequest;
import com.cohere.api.types.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** CohereAI Connector Client */
public class CohereAIConnectorClient implements ConnectorClient {
  Cohere client;

  /** Default constructor for Cohere AI Connector Client */
  public CohereAIConnectorClient() {

    client = null;
  }

  /**
   * Initializes the Cohere Client
   *
   * @param api
   */
  public CohereAIConnectorClient(String api) {

    ClientOptions clientOptions =
        new ClientOptions.Builder()
            .addHeader("Authorization", "Bearer " + api)
            .environment(Environment.PRODUCTION)
            .build();

    client = new Cohere(clientOptions);
  }

  /**
   * Allows users to have conversations with 'command' - a Large Language Model (LLM) from Cohere.
   *
   * @param question
   * @return String response
   */
  public String simpleChat(String question) {
    NonStreamedChatResponse response = client.chat(ChatRequest.builder().message(question).build());

    return response.getText();
  }

  /**
   * Allows users to have conversations with a Large Language Model (LLM) of their choice from
   * Cohere.
   *
   * @param question
   * @param model
   * @return String response
   */
  public String simpleChat(String question, String model) {
    NonStreamedChatResponse response =
        client.chat(ChatRequest.builder().message(question).model(model).build());

    return response.getText();
  }

  /**
   * Generates realistic text conditioned on a given input.
   *
   * @param question
   * @return String response
   */
  public String simpleGenerate(String question) {
    Generation response = client.generate(GenerateRequest.builder().prompt(question).build());

    return response.getGenerations().get(0).getText();
  }

  /**
   * Generates realistic text conditioned on a given input (using model passed).
   *
   * @param question
   * @param model
   * @return String response
   */
  public String simpleGenerate(String question, String model) {
    Generation response =
        client.generate(GenerateRequest.builder().prompt(question).model(model).build());

    return response.getGenerations().get(0).getText();
  }

  /**
   * Makes a prediction about which label fits the specified text inputs best.
   *
   * @param examples
   * @param question
   * @return String response (label)
   */
  public String classify(Map<String, String> examples, String question) {
    List<ClassifyRequestExamplesItem> examplesList = convertMaptoClassifyRequest(examples);

    ClassifyResponse response =
        client.classify(
            ClassifyRequest.builder().examples(examplesList).addInputs(question).build());

    return response.getClassifications().get(0).getPrediction();
  }

  /**
   * Converts a given HashMap of text and labels to a list of ClassifyRequestExamplesItem
   *
   * @param examples
   * @return A list of ClassifyRequestExamplesItem
   */
  private List<ClassifyRequestExamplesItem> convertMaptoClassifyRequest(
      Map<String, String> examples) {
    List<ClassifyRequestExamplesItem> classifyRequestExamplesItems = new ArrayList<>();

    for (Map.Entry<String, String> entry : examples.entrySet()) {
      classifyRequestExamplesItems.add(
          ClassifyRequestExamplesItem.builder()
              .text(entry.getKey())
              .label(entry.getValue())
              .build());
    }

    return classifyRequestExamplesItems;
  }

  /**
   * Returns text embeddings
   *
   * @param text
   * @return Float array of the embeddings
   */
  public Float[] embedText(String text) {
    EmbedResponse response = client.embed(EmbedRequest.builder().addTexts(text).build());

    String floatArray = response.getEmbeddings().get(0).toString();

    return convertToFloatArray(floatArray);
  }

  /**
   * Converts a string of float array into a float array
   *
   * @param floatArrayString
   * @return float array
   */
  private Float[] convertToFloatArray(String floatArrayString) {

    String[] values = floatArrayString.replaceAll("\\[|\\]", "").split(",");

    Float[] floatArray = new Float[values.length];
    for (int i = 0; i < values.length; i++) {
      floatArray[i] = Float.parseFloat(values[i]);
    }

    return floatArray;
  }

  /**
   * Retrieves the underlying client used for communication.
   *
   * @return the client object
   */
  @Override
  public Object getClient() {
    return client;
  }

  /**
   * Initializes the client with the provided API key.
   *
   * @param api the API key to use for initialization
   */
  public void initializeClient(String api) {
    ClientOptions clientOptions =
        new ClientOptions.Builder()
            .addHeader("Authorization", "Bearer " + api)
            .environment(Environment.PRODUCTION)
            .build();

    client = new Cohere(clientOptions);
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
   * This functionality is not available for this client.
   *
   * @param link the link to read data from
   * @return a list of documents
   * @throws LoaderException if there is an issue loading the data
   */
  @Override
  public List<Document> readData(String link) throws LoaderException {
    return null;
  }

  /**
   * Makes a call to the model with the context passed
   *
   * @param question
   * @param context
   * @return Response from the model
   */
  public String callWithContext(String question, List<Document> context) {
    StringBuilder mergedContent = new StringBuilder();

    for (Document doc : context) {
      mergedContent.append(doc.getText()).append(" ");
    }

    mergedContent.append(question);

    return simpleGenerate(mergedContent.toString());
  }
}
