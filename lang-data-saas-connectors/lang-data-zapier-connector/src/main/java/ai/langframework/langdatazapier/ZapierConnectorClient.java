package ai.langframework.langdatazapier;

import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

public class ZapierConnectorClient implements ConnectorClient {

    OkHttpClient client;
    String accessToken;

    /**
     *Constructor method for creating a Zapier connector client without an access token.
     */
    public ZapierConnectorClient() {
        client = null;
    }

    /**
     * Constructor method for creating a Zapier connector client with an access token.
     * @param accessToken
     */
    public ZapierConnectorClient(String accessToken) {
        this.client = new OkHttpClient();
        this.accessToken = accessToken;
    }

    /**
     *Retrieves a list of available actions from Zapier. Returns
     * @return a map where the keys are action descriptions (like "Send Email") and the values are the corresponding action IDs.
     * @throws Exception
     */
    public Map<String, String> listActions() throws Exception {
        Map<String, String> actionMap = new HashMap<>();
        String listActionsEndpoint = "https://actions.zapier.com/api/v1/exposed/";
        Request request = new Request.Builder()
                .url(listActionsEndpoint)
                .addHeader("X-API-Key", accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to list actions: " + response.code());
            }

            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONArray results = jsonObject.getJSONArray("results");

            for (int i = 0; i < results.size(); i++) {
                JSONObject result = results.getJSONObject(i);
                String id = result.getStr("id");
                String description = result.getStr("description");
                actionMap.put(description, id);
            }
        }
        return actionMap;
    }


    /**
     * Executes a specified action with given description and instructions.
     * @param  description The description of the action to execute.
     * @param instructions The instructions for the action execution.
     * @return A string indicating successful execution.
     * @throws Exception
     */
    public String executeAction(String description, String instructions) throws Exception {
        Map<String, String> actionMap = listActions();

        if (!actionMap.containsKey(description)) {
            throw new IllegalArgumentException("Action description not found: " + description);
        }

        String actionId = actionMap.get(description);
        String url = "https://actions.zapier.com/api/v1/dynamic/exposed/" + actionId + "/execute/";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("instructions", instructions);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-API-Key", accessToken)
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to execute action: " + response.code());
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);

            String error = jsonResponse.getStr("error");
            if (error != null && !error.isEmpty()) {
                throw new Exception("Action execution error: " + error);
            }

            return "Executed Successfully";
        }
    }

    /**
     * Returns the underlying HTTP client used for making requests.
     * @return The HTTP client object.
     */
    @Override
    public Object getClient() { return client; }

    /**
     * Reads data from the specified file.
     * @param file The file from which to read data.
     * @return A Document object representing the read data.
     * @throws LoaderException If an error occurs during data reading.
     */
    @Override
    public List<Document> readData(File file) throws LoaderException {
        return null;
    }

    /**
     * Reads data from the specified link.
     * @param link The link from which to read data.
     * @return A Document object representing the read data.
     * @throws LoaderException If an error occurs during data reading.
     */
    @Override
    public List<Document> readData(String link) throws LoaderException {
        return null;
    }
}
