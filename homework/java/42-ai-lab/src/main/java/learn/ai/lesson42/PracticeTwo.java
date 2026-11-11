package learn.ai.lesson42;

import dev.langchain4j.model.chat.ChatModel;
import java.util.List;
import java.util.Optional;

/**
 * 练习2 修改：在资料末尾追加发票段，再问「发票多久开好」。
 * 「发票」二字只出现在新段落；发货段有「发货」没有「发票」，不会因为都提到发货时间而命中发货。
 * 检索对了，替身不认识这段资料，仍回「不知道。」——检索和模型是两步。
 */
public final class PracticeTwo {
    public static void main(String[] args) throws Exception {
        String question = "发票多久开好";
        KeywordCorpus corpus = new KeywordCorpus(List.of(
                new Passage("退款", "签收后7天内可申请退款。质量问题由店铺承担运费。"),
                new Passage("发货", "成都仓在工作日16:00前付款的订单当天发货，默认使用顺丰。"),
                new Passage("发票", "电子发票在发货后24小时内开具。")));
        Optional<Passage> hit = corpus.best(question);
        System.out.println("检索命中：" + hit.map(Passage::title).orElse("无"));
        System.out.println("资料：" + hit.map(Passage::text).orElse("（没有检索到资料）"));
        try (LocalChatCompletionServer server = new LocalChatCompletionServer()) {
            ChatModel model = CompatibleChatModels.create(
                    new AiConfig(server.baseUrl(), LocalChatCompletionServer.DEMO_TOKEN, "local-echo"));
            System.out.println("本地兼容接口回答：" + model.chat(RagPrompts.userPrompt(hit, question)));
        }
    }
}
