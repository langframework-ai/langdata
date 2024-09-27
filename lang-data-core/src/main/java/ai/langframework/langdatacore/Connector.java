package ai.langframework.langdatacore;

import ai.langframework.langdatacore.exceptions.LoaderException;
import java.io.File;
import java.util.List;

public interface Connector {
  List<Document> loadData(File file) throws LoaderException, LoaderException;

  List<Document> loadData(String link) throws LoaderException;
}
