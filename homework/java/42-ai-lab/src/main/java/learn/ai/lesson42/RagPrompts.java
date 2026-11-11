package learn.ai.lesson42;

import java.util.Optional;

public final class RagPrompts {
    private RagPrompts() {}

    public static String userPrompt(Optional<Passage> hit, String question) {
        String material = hit.map(passage -> passage.title() + "：" + passage.text()).orElse("（没有检索到资料）");
        return "只根据资料回答，资料不足就说不知道。\n资料：" + material + "\n问题：" + question;
    }
}
