package learn.ai.lesson42;

import java.util.Map;

/**
 * 兼容接口的三个设置。密钥只从环境变量或 .env 进入，toString 不包含密钥原文。
 */
public record AiConfig(String baseUrl, String apiKey, String modelName) {
    public static final String DEFAULT_BASE_URL = "https://api.deepseek.com/v1";
    public static final String DEFAULT_MODEL = "deepseek-chat";

    public AiConfig {
        if (baseUrl == null || baseUrl.isBlank()) baseUrl = DEFAULT_BASE_URL;
        else baseUrl = baseUrl.trim();
        if (modelName == null || modelName.isBlank()) modelName = DEFAULT_MODEL;
        else modelName = modelName.trim();
        apiKey = apiKey == null ? "" : apiKey.trim();
    }

    public boolean hasKey() {
        return !apiKey.isBlank();
    }

    /** 同名环境变量覆盖文件。空白环境变量视为没设置。 */
    public static AiConfig from(Map<String, String> env, Map<String, String> file) {
        Map<String, String> environment = env == null ? Map.of() : env;
        Map<String, String> dotenv = file == null ? Map.of() : file;
        return new AiConfig(
                first(environment, dotenv, "AI_BASE_URL"),
                first(environment, dotenv, "AI_API_KEY"),
                first(environment, dotenv, "AI_MODEL"));
    }

    private static String first(Map<String, String> env, Map<String, String> file, String key) {
        String fromEnv = env.get(key);
        if (fromEnv != null && !fromEnv.isBlank()) return fromEnv;
        String fromFile = file.get(key);
        return fromFile == null ? "" : fromFile;
    }

    @Override
    public String toString() {
        return "AiConfig[baseUrl=" + baseUrl + ", modelName=" + modelName + ", apiKey=" + (hasKey() ? "已设置" : "未设置") + "]";
    }
}
