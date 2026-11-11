package learn.ai.lesson42;

import dev.langchain4j.model.openai.OpenAiChatModel;
import java.time.Duration;

public final class CompatibleChatModels {
    private CompatibleChatModels() {}

    /** 没有密钥时直接拒绝，避免带着空的 Authorization 去访问默认公网地址。 */
    public static OpenAiChatModel create(AiConfig config) {
        if (config == null || !config.hasKey()) throw new IllegalStateException("缺少 AI_API_KEY");
        return OpenAiChatModel.builder()
                .baseUrl(config.baseUrl())
                .apiKey(config.apiKey())
                .modelName(config.modelName())
                .temperature(0.0)
                .timeout(Duration.ofSeconds(30))
                .maxRetries(1)
                .logRequests(false)
                .logResponses(false)
                .build();
    }
}
