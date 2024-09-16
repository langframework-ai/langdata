package ai.langframework.langdatacore;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    private static final Dotenv dotenv = Dotenv.load();

    public static String getApiKey(String name) {
        // Try to get the API key from the system environment variable. If not found, try to get it from the .env file
        String apiKey = System.getenv(name);
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = dotenv.get(name);
        }
        // Throw an exception or return a default value if the API key is still not found
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("API key is not set in the environment variables or .env file");
        }
        return apiKey;
    }

    public static boolean isApiKeySet(String name) {
        // Checks whether a particular environment variable is set.
        return getApiKey(name) != null && !getApiKey(name).isEmpty();
    }
}
