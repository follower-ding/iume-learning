package learn.ai.lesson42;

import dev.langchain4j.model.chat.ChatModel;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class AiLabDemo {
    private AiLabDemo() {}

    public static void main(String[] args) throws Exception {
        run(args, System.getenv(), DotEnv.defaultPath());
    }

    static void run(String[] args, Map<String, String> env, Path dotenv) throws Exception {
        if (args != null && List.of(args).contains("--live")) {
            live(AiConfig.from(env, DotEnv.load(dotenv)));
            return;
        }
        rehearse();
    }

    private static void rehearse() throws Exception {
        String question = "几天内可以退款？";
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
        System.out.println("真实接口未调用。复制 07-ai-lab/.env.example 为 07-ai-lab/.env 并填写 AI_API_KEY 后，加上 --live 才会访问兼容服务。");
    }

    private static void live(AiConfig config) {
        if (!config.hasKey()) {
            System.out.println("缺少 AI_API_KEY，未发起网络请求。");
            return;
        }
        ChatModel model = CompatibleChatModels.create(config);
        System.out.println("使用配置：" + config);
        System.out.println("直接对话：" + model.chat("用一个词回答：你好"));
        String question = "几天内可以退款？";
        Optional<Passage> hit = KeywordCorpus.shopPolicies().best(question);
        System.out.println("检索命中：" + hit.map(Passage::title).orElse("无"));
        System.out.println("资料问答：" + model.chat(RagPrompts.userPrompt(hit, question)));
        System.out.println("工具问答：" + OrderAssistants.create(model, OrderLookup.sample()).chat("查询订单 1001 的状态"));
    }
}
