package learn.ai.lesson42;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;

public final class OrderAssistants {
    private OrderAssistants() {}

    public static OrderAssistant create(ChatModel model, OrderLookup lookup) {
        return AiServices.builder(OrderAssistant.class).chatModel(model).tools(lookup).build();
    }
}
