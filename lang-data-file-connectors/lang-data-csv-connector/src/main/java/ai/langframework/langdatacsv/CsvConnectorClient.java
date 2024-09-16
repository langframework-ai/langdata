package ai.langframework.langdatacsv;

import ai.langframework.langdatacore.ConnectorClient;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CSV Connector Client
 */
public class CsvConnectorClient implements ConnectorClient {
    /**
     * Reads data from a CSV file and converts it into a list of documents.
     * @param file the CSV file to read data from
     * @return a list of documents containing the data from the CSV file
     */
    @Override
    public List<Document> readData(File file) {
        List<Document> documents = new ArrayList<Document>();
        try{
            FileReader reader = new FileReader(file);
            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(reader);

            List<String> headerNames = csvParser.getHeaderNames();

            int totalColumns = headerNames.size();

            for (CSVRecord record : csvParser) {
                String documentText = "";
                HashMap<String, String> metadata = new HashMap<>();

                for(int i=0; i<totalColumns; i++){
                    documentText += headerNames.get(i) + ":";
                    if(i==totalColumns-1){
                        documentText += record.get(headerNames.get(i));
                    }
                    else{
                        documentText += record.get(headerNames.get(i)) + "\\n";
                    }
                }
                metadata.put("FileName", file.getName());
                metadata.put("Source", file.getAbsolutePath());
                metadata.put("FileSize", String.valueOf(file.length()));
                metadata.put("Row",String.valueOf(record.getRecordNumber()));
                Document newDocument = new Document(documentText,metadata);
                documents.add(newDocument);
            }

            csvParser.close();
            reader.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return documents;
    }


    /**
     * Reads data from a CSV file located at the specified link and converts it into a list of documents.
     * @param link the link to the CSV file
     * @return a list of documents containing the data from the CSV file
     */
    @Override
    public List<Document> readData(String link) {
        URL url = null;
        List<Document> documents = new ArrayList<Document>();
        try{
            url= new URL(link);
            InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
            BufferedReader reader = new BufferedReader(inputStreamReader);

            CSVParser csvParser = CSVFormat.DEFAULT.withHeader().parse(reader);
            List<String> headerNames = csvParser.getHeaderNames();

            int totalColumns = headerNames.size();

            for (CSVRecord record : csvParser) {
                String documentText = "";
                HashMap<String, String> metadata = new HashMap<>();

                for(int i=0; i<totalColumns; i++){
                    documentText += headerNames.get(i) + ":";
                    if(i==totalColumns-1){
                        documentText += record.get(headerNames.get(i));
                    }
                    else{
                        documentText += record.get(headerNames.get(i)) + "\\n";
                    }
                }
                metadata.put("FileName", url.getFile());
                metadata.put("Source", link);
                metadata.put("FileSize", String.valueOf(url.getFile().length()));
                metadata.put("Row",String.valueOf(record.getRecordNumber()));
                Document newDocument = new Document(documentText,metadata);
                documents.add(newDocument);
            }

            csvParser.close();
            reader.close();

        }
        catch (Exception e){
            e.printStackTrace();
        }
        for(Document doc: documents){
            Logger.info(doc.getText());
            Logger.info(doc.getMetadata());
        }
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
