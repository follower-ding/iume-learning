package learn.ai.lesson42;

import dev.langchain4j.model.chat.ChatModel;
import java.util.Optional;

/** 练习1 模仿：问题改成「什么时候发货」，预测命中发货段；只走本机替身，不加 --live。 */
public final class PracticeOne {
    public static void main(String[] args) throws Exception {
        String question = "什么时候发货";
        KeywordCorpus corpus = KeywordCorpus.shopPolicies();
        Optional<Passage> hit = corpus.best(question);
        System.out.println("检索命中：" + hit.map(Passage::title).orElse("无"));
        System.out.println("资料：" + hit.map(Passage::text).orElse("（没有检索到资料）"));
        try (LocalChatCompletionServer server = new LocalChatCompletionServer()) {
            ChatModel model = CompatibleChatModels.create(
                    new AiConfig(server.baseUrl(), LocalChatCompletionServer.DEMO_TOKEN, "local-echo"));
            System.out.println("本地兼容接口回答：" + model.chat(RagPrompts.userPrompt(hit, question)));
            OrderLookup lookup = OrderLookup.sample();
            System.out.println("本地工具调用回答：" + OrderAssistants.create(model, lookup).chat("查询订单 1001 的状态"));
        }
    }
}
