package ai.langframework.langdatadocx;

import ai.langframework.langdatacore.Connector;
import ai.langframework.langdatacore.Document;
import ai.langframework.langdatacore.exceptions.LoaderException;

import java.io.File;
import java.util.List;


/**
 * Microsoft Word Document Connector
 */
public class DocxConnector implements Connector {

    /**
     * Loads data from a DOCX file using a DocxConnectorClient.
     * @param file the DOCX file to load data from
     * @return a list of documents containing the data from the DOCX file
     */
    @Override
    public List<Document> loadData(File file) throws LoaderException {
        return new DocxConnectorClient().readData(file);
    }

    /**
     * Loads data from a DOCX file located at the specified link using a DocxConnectorClient.
     * @param link the link to the DOCX file
     * @return a list of documents containing the data from the DOCX file
     * @throws LoaderException if there is an issue loading the data
     */
    @Override
    public List<Document> loadData(String link) throws LoaderException {
        return new DocxConnectorClient().readData(link);
    }

}
