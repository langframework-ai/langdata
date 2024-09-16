package ai.langframework.langdatacore;

import ai.langframework.langdatacore.exceptions.LoaderException;

public abstract class AbstractLoader implements Connector {
    protected String data;

    public void load(String source) throws LoaderException {
        Logger.info("Loading source" + source);
    }

}
