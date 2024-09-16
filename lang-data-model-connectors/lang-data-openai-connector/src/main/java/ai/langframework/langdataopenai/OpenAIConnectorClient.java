package ai.langframework.langdataopenai;

import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAIConnectorClient implements ConnectorClient {

    private OpenAiService  client;
    private String chatModel = "gpt-3.5-turbo";
    private Double temperature = 0.8;

    public OpenAIConnectorClient(){
        client = null;
    }

    public OpenAIConnectorClient(String apikey){
        client = new OpenAiService(apikey);
    }

    public enum MessageType {
        USER,
        ASSISTANT
    }

    /**
     * Performs a simple chat with the given question and model.
     * @param question the question to ask
     * @param model the model to use for completion
     * @return the response from the model
     */
    public String simpleChat(String question,String model){
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(question)
                .model(model)
                .echo(true)
                .build();
        List<CompletionChoice> choices = client.createCompletion(completionRequest).getChoices();
        return choices.get(0).getText();
    }

    /**
     * Initiates a chat conversation with a list of messages, using a specified model and temperature.
     * @param messages the map of chat messages exchanged so far
     * @param model the model to use for completion
     * @param temperature the temperature parameter for generation
     * @return the response from the model
     */
    public String chatMessage(Map<MessageType, String> messages, String model, Double temperature) {
        List<ChatMessage> chatMessages = new ArrayList<>();

        for (Map.Entry<MessageType, String> entry : messages.entrySet()) {
            MessageType type = entry.getKey();
            String content = entry.getValue();
            switch (type){
                case USER:
                    chatMessages.add(new ChatMessage("user", content));
                    break;
                case ASSISTANT:
                    chatMessages.add(new ChatMessage("assistant", content));
                    break;
            }
        }

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model(model)
                .temperature(temperature)
                .messages(chatMessages)
                .build();

        StringBuilder builder = new StringBuilder();
        client.createChatCompletion(chatCompletionRequest)
                .getChoices().forEach(choice -> {
                    builder.append(choice.getMessage().getContent());
                });
        return builder.toString();
    }

    /**
     * Generates embeddings for a list of texts using a specified model.
     * @param texts the list of texts to generate embeddings for
     * @param model the model to use for embedding generation
     * @return a list of embeddings generated for the input texts
     */
    public List<Float[]> embedding(List<String> texts, String model) {
        EmbeddingRequest completionRequest = EmbeddingRequest.builder()
                .model(model)
                .input(texts).build();
        return convertEmbeddings(client.createEmbeddings(completionRequest).getData());
    }

    /**
     * This functionality is not available for this client.
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
     * @param link the link to read data from
     * @return a list of documents
     * @throws LoaderException if there is an issue loading the data
     */
    @Override
    public List<Document> readData(String link) throws LoaderException {
        return null;
    }

    /**
     * Sets the openAI chat model to use
     * @param chatModel
     */
    public void setChatModel(String chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * Sets the temperature for the calls
     * @param temperature
     */
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    /**
     * Retrieves the underlying OpenAiService client.
     * @return the OpenAiService client
     */
    @Override
    public Object getClient() {
        return client;
    }

    /**
     * Initializes the connector with the provided API key.
     * @param apikey the API key to use for initializing the client
     */
    public void initializeClient(String apikey) {
        this.client = new OpenAiService(apikey);;
    }

    /**
     * Makes a call to the model with the context passed
     * @param question
     * @param context
     * @return Response from the model
     */
    public String callWithContext(String question, List<Document> context){
        StringBuilder mergedContent = new StringBuilder();

        for (Document doc : context) {
            mergedContent.append(doc.getText()).append(" ");
        }

        mergedContent.append(question);

        Map<MessageType, String > messages = new HashMap<>();
        messages.put(MessageType.USER, mergedContent.toString());

        return chatMessage(messages, chatModel, temperature);
    }

    /**
     * Converts the List of Embedding object into a List of Float array
     * @param embeddings
     * @return List of float array
     */
    private List<Float[]> convertEmbeddings(List<Embedding> embeddings) {
        List<Float[]> resultList = new ArrayList<>();

        for (Embedding embedding : embeddings) {
            List<Double> embeddingValues = embedding.getEmbedding();
            int embeddingSize = embeddingValues.size();
            Float[] result = new Float[embeddingSize];

            for (int i = 0; i < embeddingSize; i++) {
                result[i] = embeddingValues.get(i).floatValue();
            }

            resultList.add(result);
        }

        return resultList;
    }
}
