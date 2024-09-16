package ai.langframework.langdatatext;

import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Text Connector Client
 */
public class TextConnectorClient implements ConnectorClient {
    /**
     * Reads data from a text file and creates documents.
     * @param file The file to read data from.
     * @return The list of created documents.
     */
    @Override
    public List<Document> readData(File file) {
        List<Document> documents = new ArrayList<>();

        String text = "";
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                // process the line
                text += line;
            }

            fileReader.close();
            bufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, String> metadata = new HashMap<>();
        metadata.put("FileName", file.getName());
        metadata.put("Source", file.getAbsolutePath());
        metadata.put("FileSize", String.valueOf(file.length()));


        documents.add(new Document(text, metadata));
        return documents;
    }

    /**
     * Reads data from a text file located at the given link and creates documents.
     * @param link The link to read data from.
     * @return The list of created documents.
     */
    @Override
    public List<Document> readData(String link){
        List<Document> documents = new ArrayList<>();

        String text = "";
        URL url = null;
        try{
            url= new URL(link);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;

            while ((line = in.readLine()) != null){
                text += line;
                Logger.info(line);
            }
            in.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        HashMap<String, String> metadata = new HashMap<>();
        metadata.put("Source", link);
        metadata.put("FileName", url.getFile());
        metadata.put("FileSize", String.valueOf(url.getFile().length()));

        documents.add(new Document(text, metadata));

        return documents;
    }

    /**
     * Gets the client object.
     * @return The client object.
     */
    @Override
    public Object getClient() {
        return this;
    }
}
