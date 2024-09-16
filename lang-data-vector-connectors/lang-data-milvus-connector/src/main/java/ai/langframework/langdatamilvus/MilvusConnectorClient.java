package ai.langframework.langdatamilvus;

import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.VectorInterface;
import ai.langframework.langdatacohereai.CohereAIConnectorClient;
import ai.langframework.langdataopenai.OpenAIConnectorClient;
import ai.langframework.langdatacore.exceptions.LoaderException;
import com.alibaba.fastjson.JSONObject;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DescribeCollectionResponse;
import io.milvus.param.ConnectParam;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.DescribeCollectionParam;
import io.milvus.param.collection.DropCollectionParam;
import io.milvus.param.highlevel.collection.CreateSimpleCollectionParam;
import io.milvus.param.highlevel.dml.DeleteIdsParam;
import io.milvus.param.highlevel.dml.GetIdsParam;
import io.milvus.param.highlevel.dml.InsertRowsParam;
import io.milvus.param.highlevel.dml.SearchSimpleParam;
import io.milvus.param.highlevel.dml.response.DeleteResponse;
import io.milvus.param.highlevel.dml.response.GetResponse;
import io.milvus.param.highlevel.dml.response.InsertResponse;
import io.milvus.param.highlevel.dml.response.SearchResponse;
import io.milvus.response.DescCollResponseWrapper;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.QueryResultsWrapper.RowRecord;

import java.io.File;
import java.util.*;

/**
 * Milvus Connector Client
 */
public class MilvusConnectorClient implements ConnectorClient, VectorInterface {

    private  MilvusServiceClient client;
    private String collectionName;
    private String clusterEndpoint;
    private String token;
    private EmbeddingClientType embeddingClientType;
    private OpenAIConnectorClient modelOpenAIConnectorClient;
    private CohereAIConnectorClient modelCohereConnectorClient;
    private String embeddingModel = "text-embedding-ada-002";
    private long offset = 0L;
    private long limit = 10L;

    /**
     * Default constructor for MilvusConnectorClient.
     * Initializes the client to null.
     */
    public MilvusConnectorClient()
    {
        client = null;
    }

    /**
     * Constructor for MilvusConnectorClient.
     * Connects to Zilliz Cloud using the provided cluster endpoint and token.
     * @param clusterEndpoint The endpoint of the Milvus cluster.
     * @param token The authentication token for accessing the cluster.
     */
    public MilvusConnectorClient(String clusterEndpoint, String token)
    {
        // 1. Connect to Zilliz Cloud

        ConnectParam connectParam = ConnectParam.newBuilder()
                .withUri(clusterEndpoint)
                .withToken(token)
                .build();

        client = new MilvusServiceClient(connectParam);
    }

    /**
     * Constructs a new Milvus connector client with the specified cluster endpoint, token,
     * embedding client type, and model API key.
     * @param clusterEndpoint      The endpoint of the Milvus cluster.
     * @param token                The token for authentication.
     * @param embeddingClientType  The type of embedding client to use.
     * @param modelApiKey          The API key for the embedding model.
     */
    public MilvusConnectorClient(String clusterEndpoint, String token, EmbeddingClientType embeddingClientType, String modelApiKey )
    {
        try{
            ConnectParam connectParam = ConnectParam.newBuilder()
                    .withUri(clusterEndpoint)
                    .withToken(token)
                    .build();

            client = new MilvusServiceClient(connectParam);

            this.embeddingClientType = embeddingClientType;

            switch (embeddingClientType){
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
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Sets the offset for queries
     * @param offset
     */
    public void setOffset(long offset) {
        this.offset = offset;
    }

    /**
     * Sets the limit for queries
     * @param limit
     */
    public void setLimit(long limit) {
        this.limit = limit;
    }

    /**
     * Sets the embedding model
     * @param embeddingModel
     */
    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * Creates a new collection in the Milvus cluster with the specified name and dimension.
     * @param collectionName The name of the collection to create.
     * @param dimension The dimensionality of the vectors in the collection.
     */
    public void createCollection(String collectionName, Integer dimension)
    {
        this.collectionName = collectionName;
        CreateSimpleCollectionParam createCollectionParam = CreateSimpleCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withDimension(dimension)
                .build();

        R<RpcStatus> createCollection = client.createCollection(createCollectionParam);

        if (createCollection.getException() != null) {
            System.err.println("Failed to create collection: " + createCollection.getException().getMessage());
            return;
        }
        System.out.println("Collection Created Successfully");
    }


    /**
     * Describes the specified collection in the Milvus cluster.
     * @param collectionName
     * @return The collection object in String format
     */
    public String describeCollection(String collectionName)
    {
        DescribeCollectionParam describeCollectionParam = DescribeCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();

        R<DescribeCollectionResponse> collectionInfo = client.describeCollection(describeCollectionParam);

        DescCollResponseWrapper wrapperDescribeCollection = new DescCollResponseWrapper(collectionInfo.getData());

        return wrapperDescribeCollection.toString();
    }

    /**
     * The `insertEntity` method inserts entities into the specified collection using the Milvus service client.
     * It returns "Inserted Successfully" upon successful insertion.
     * @param rows
     */
    public String insertEntity(List<JSONObject> rows)
    {
        InsertRowsParam insertRowsParam = InsertRowsParam.newBuilder()
                .withCollectionName(collectionName)
                .withRows(rows)
                .build();

        R<InsertResponse> response = client.insert(insertRowsParam);
        handleResponseStatus(response);

        if (response.getException() != null) {
            return("Failed to insert");

        }
        return("Inserted Successfully");
    }

    /**
     * Handles the response status of the calls
     * @param r
     */
    private void handleResponseStatus(R<?> r) {
        if (r.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException(r.getMessage());
        }
    }

    /**
     * The `SearchANN` method performs an approximate nearest neighbor search in the specified collection using the given query vector and parameters.
     * @param queryVector1
     * @param outputFields
     * @param offset
     * @param limit
     * @return a list of row records representing the search results or an empty list if an error occurs during the search operation.
     */
    private  List<RowRecord> SearchANN( List<Float>  queryVector1, List<String> outputFields, Long offset, Long limit)
    {
        List<List<Float>> queryVectors = new ArrayList<>();
        queryVectors.add(queryVector1);

        // Build dynamic search parameters
        SearchSimpleParam searchSimpleParam = SearchSimpleParam.newBuilder()
                .withCollectionName(collectionName)
                .withVectors(queryVectors)
                .withOutputFields(outputFields)
                .withOffset(offset)
                .withLimit(limit)
                .build();

        // Perform search
        R<SearchResponse> searchRes = client.search(searchSimpleParam);

        if (searchRes.getException() != null) {
            System.err.println("Failed to search: " + searchRes.getException().getMessage());
            return Collections.emptyList();
        }

        // Process and return search results
        return new ArrayList<>(searchRes.getData().getRowRecords(0));

    }

    /**
     * The `SearchANN` method performs an approximate nearest neighbor search in the specified collection using the given query vector and parameters.
     * It returns a list of row records representing the search results or an empty list if an error occurs during the search operation.
     * @param queryVector1
     * @param outputFields
     * @param offset
     * @param limit
     * @return a list of row records representing the search results or an empty list if an error occurs during the search operation.
     */
    public String searchApproximateNearestNeighbor(List<Float> queryVector1, List<String> outputFields, Long offset, Long limit) {
        try {
            List<List<Float>> queryVectors = new ArrayList<>();
            queryVectors.add(queryVector1);

            // Build dynamic search parameters
            SearchSimpleParam searchSimpleParam = SearchSimpleParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withVectors(queryVectors)
                    .withOutputFields(outputFields)
                    .withOffset(offset)
                    .withLimit(limit)
                    .build();

            // Perform search
            R<SearchResponse> searchRes = client.search(searchSimpleParam);

            if (searchRes.getException() != null) {
                System.err.println("Failed to search: " + searchRes.getException().getMessage());
                return "Search failed: " + searchRes.getException().getMessage();
            }
            // Process and return search results
            StringBuilder resultStringBuilder = new StringBuilder();
            List<RowRecord> rowRecords = new ArrayList<>(searchRes.getData().getRowRecords(0));
            resultStringBuilder.append("Search Results:\n");
            for (RowRecord rowRecord : rowRecords) {
                resultStringBuilder.append(rowRecord.toString()).append("\n");
            }
            return resultStringBuilder.toString();
        } catch (Exception e) {
            // Handle exceptions appropriately, e.g., logging or rethrowing
            return "Search failed: " + e.getMessage();
        }
    }
    /**
     * The `SearchWithFilters` method conducts a search in the specified collection using the given query vector and filters.
     * If an exception occurs during the search operation, it prints an error message and returns an empty list.
     * @param queryVector1
     * @param outputFields
     * @param offset
     * @param limit
     * @param filter
     * @return A list of row records representing the search results, applying pagination if specified.
     */
    public String searchWithFilters(List<Float> queryVector1, List<String> outputFields, Long offset, Long limit, String filter) {
        try {
            List<List<Float>> queryVectors = new ArrayList<>();
            queryVectors.add(queryVector1);

            SearchSimpleParam searchSimpleParam = SearchSimpleParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withVectors(queryVectors)
                    .withOutputFields(outputFields)
                    .withFilter(filter)
                    .withOffset(offset)
                    .withLimit(limit)
                    .build();

            R<SearchResponse> searchRes = client.search(searchSimpleParam);

            if (searchRes.getException() != null) {
                System.err.println("Failed to search: " + searchRes.getException().getMessage());
                return "Search failed: " + searchRes.getException().getMessage();
            }

            StringBuilder resultStringBuilder = new StringBuilder();
            resultStringBuilder.append("Search Results:\n");
            for (QueryResultsWrapper.RowRecord rowRecord : searchRes.getData().getRowRecords(0)) {
                resultStringBuilder.append(rowRecord.toString()).append("\n");
            }

            return resultStringBuilder.toString();
        } catch (Exception e) {
            return "Search failed: " + e.getMessage();
        }
    }


    /**
     * The `searchWithIds` method retrieves records from the specified collection based on the provided list of IDs.
     * If an exception occurs during the retrieval operation, it prints an error message.
     * @param collectionName
     * @param outputFields
     * @param ids
     * @return String response representing a list of row records corresponding to the IDs.
     */
    public String searchWithIds(String collectionName, List<String> outputFields, List<String> ids) {
        try {
            GetIdsParam getParam = GetIdsParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withPrimaryIds(ids)
                    .withOutputFields(outputFields)
                    .build();

            R<GetResponse> getRes = client.get(getParam);

            if (getRes.getException() != null) {
                System.err.println("Failed to get: " + getRes.getException().getMessage());
                return "Get failed: " + getRes.getException().getMessage();
            }

            StringBuilder resultStringBuilder = new StringBuilder();
            resultStringBuilder.append("Search Results:\n");
            for (QueryResultsWrapper.RowRecord rowRecord : getRes.getData().getRowRecords()) {
                resultStringBuilder.append(rowRecord.toString()).append("\n");
            }

            return resultStringBuilder.toString();
        } catch (Exception e) {
            return "Get failed: " + e.getMessage();
        }
    }
    /**
     * The `deleteEntities` method removes entities from the given collection using the provided list of IDs.
     * It returns "Successfully deleted" upon success or "Failed to delete entity"if there's an error.
     * @param collectionName
     * @param id
     * @return "Successfully deleted" upon success or "Failed to delete entity" if there's an error.
     */
    public String deleteEntities(String collectionName, List<String> id)
    {
        // (Continued)
        List<String> ids = id;
        // List<String> ids = Lists.newArrayList("1", "2", "3");

        DeleteIdsParam deleteParam = DeleteIdsParam.newBuilder()
                .withCollectionName(collectionName)
                .withPrimaryIds(ids)
                .build();

        R<DeleteResponse> deleteRes = client.delete(deleteParam);

        if (deleteRes.getException() != null) {
            return ("Failed to delete entity");
        }

        return ("Successfully deleted");
    }

    /**
     * The `dropCollection` method deletes the specified collection.
     * @param collectionName
     * @return "Successfully dropped collection" upon success or prints an error message and returns null if the operation fails.
     */
    public String dropCollection(String collectionName)
    {
        // (Continued)
        DropCollectionParam dropCollectionParam = DropCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();

        R<RpcStatus> dropCollection = client.dropCollection(dropCollectionParam);

        if (dropCollection.getException() != null) {
            System.err.println("Failed to drop collection: " + dropCollection.getException().getMessage());

        }

        return ("Successfully dropped collection");
    }

    /**
     * The `getClient` method retrieves the client object associated with the current instance.
     * It returns the client object.
     * @return
     */
    @Override
    public Object getClient() {
        return client;
    }

    /**
     * The `setEndpoint` method updates the endpoint used to connect to the cluster.
     * @param clusterEndpoint
     */
    public void setEndpoint(String clusterEndpoint){
        this.clusterEndpoint = clusterEndpoint;
    }

    /**
     * The `setToken` method updates the token used for authentication.
     * @param token
     */
    public void setToken(String token){
        this.token = token;
    }

    /**
     * Sets the name of the collection.
     * @param collectionName The name of the collection.
     */
    public void setCollectionName(String collectionName)
    {
        this.collectionName = collectionName;
    }

    /**
     * Retrieves the name of the collection.
     * @return The name of the collection.
     */
    public String getCollectionName( )
    {
        return collectionName;
    }

    /**
     * The `initializeClient` method sets up the client with the specified cluster endpoint and authentication token.
     * @param clusterEndpoint
     * @param token
     */
    public void initializeClient(String clusterEndpoint, String token)
    {
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withUri(clusterEndpoint)
                .withToken(token)
                .build();

        client = new MilvusServiceClient(connectParam);
    }
    /**
     * This functionality is not available for this client.
     * @param file The file to read data from
     * @return A list of documents
     * @throws LoaderException If an error occurs during loading
     */
    @Override
    public List<Document> readData(File file) throws LoaderException {
        return null;
    }
    /**
     * This functionality is not available for this client.
     * @param link The link to read data from
     * @return A list of documents
     * @throws LoaderException If an error occurs during loading
     */
    @Override
    public List<Document> readData(String link) throws LoaderException {
        return null;
    }

    /**
     * Accepts a list of documents, convert it onto vectors and stores it into Milvus
     * @param documents
     */
    @Override
    public void addDocuments( List<Document> documents) {
        try {
            if(collectionName == null){
                throw new Exception("Collection Name not set");
            }
            if (embeddingClientType == EmbeddingClientType.OPENAI) {

                List<JSONObject> jsonObject = new ArrayList<com.alibaba.fastjson.JSONObject>();

                for (Document doc : documents) {
                    JSONObject row = new JSONObject();
                    List<Float> floatList = new ArrayList<>();
                    row.put("text", doc.getText());

                    List<Float[]> data = modelOpenAIConnectorClient.embedding(List.of(doc.getText()), embeddingModel);

                    for (Float[] array : data) {
                        for (Float value : array) {
                            floatList.add(value);
                        }
                    }

                    row.put("vector", floatList);
                    row.put("id", generateRandomid().longValue());
                    jsonObject.add(row);
                }

                insertEntity(jsonObject);

            } else if (embeddingClientType == EmbeddingClientType.COHERE) {

                List<JSONObject> jsonObject = new ArrayList<com.alibaba.fastjson.JSONObject>();

                for (Document doc : documents) {
                    JSONObject row = new JSONObject();
                    List<Float> floatList = new ArrayList<>();

                    row.put("text", doc.getText());

                    Float[] data = modelCohereConnectorClient.embedText(doc.getText());

                    for (Float f : data) {
                        floatList.add(f);
                    }

                    row.put("vector", floatList);
                    row.put("id", generateRandomid().longValue());

                    jsonObject.add(row);
                }
                insertEntity(jsonObject);

            } else {
                throw new Exception("Unsupported Embedding Client Type");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Returns a list of documents that are similar to the query passed
     * @param query
     * @return
     */
    @Override
    public List<Document> searchSimilarity(String query) {
        List<Document> documents = new ArrayList<>();

        try {
            if(collectionName == null){
                throw new Exception("Collection Name not set");
            }
            if (embeddingClientType == EmbeddingClientType.OPENAI) {

                List<String> outputFields = new ArrayList<>();
                outputFields.add("text");
                outputFields.add("vector");

                List<Float> floatList = new ArrayList<>();

                List<Float[]> data = modelOpenAIConnectorClient.embedding(List.of(query), embeddingModel);

                for (Float[] array : data) {
                    for (Float value : array) {
                        floatList.add(value);
                    }
                }

                List<RowRecord> result = SearchANN(floatList, outputFields, offset, limit);

                for(RowRecord row: result){
                    HashMap<String, String> metadata = new HashMap<>();
                    Object text = row.get("text");
                    Object vector = row.get("vector");
                    metadata.put("vector", vector.toString());
                    documents.add(new Document((String) text, metadata));
                }

                return documents;

            } else if (embeddingClientType == EmbeddingClientType.COHERE) {

                List<String> outputFields = new ArrayList<>();
                outputFields.add("text");
                outputFields.add("vector");

                List<Float> floatList = new ArrayList<>();

                Float[] data = modelCohereConnectorClient.embedText(query);

                for (Float f : data) {
                    floatList.add(f);
                }

                List<RowRecord> result = SearchANN(floatList, outputFields, offset, limit);

                for(RowRecord row: result){
                    HashMap<String, String> metadata = new HashMap<>();
                    Object text = row.get("text");
                    Object vector = row.get("vector");
                    metadata.put("vector", (String) vector);
                    documents.add(new Document((String) text, metadata));
                }
                return documents;

            } else {
                throw new Exception("Unsupported Embedding Client Type");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Generates random Integer Ids of length 3
     * @return Integer Id
     */
    private Integer generateRandomid() {
        final int length = 3;
        final String CHARACTERS = "0123456789";
        Random random = new Random();

        // Generating a random integer between 0 and Integer.MAX_VALUE
        return random.nextInt();
    }
}
