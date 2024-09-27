package ai.langframework.langdatacore;

import ai.langframework.langdatacore.exceptions.LoaderException;
import java.io.File;
import java.util.List;

public interface ConnectorClient {
  Object getClient();

  List<Document> readData(File file) throws LoaderException;

  List<Document> readData(String link) throws LoaderException;
}
