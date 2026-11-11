package learn.ai.lesson42;

import java.util.List;
import java.util.Optional;

/**
 * 用重叠的二字片段在内存资料里找最相关的一段。
 * 这不是向量检索：没有嵌入模型，也没有外部库，方便先看清“先检索、再提问”。
 */
public final class KeywordCorpus {
    private final List<Passage> passages;

    public KeywordCorpus(List<Passage> passages) {
        if (passages == null || passages.isEmpty()) throw new IllegalArgumentException("资料不能为空");
        this.passages = List.copyOf(passages);
    }

    public static KeywordCorpus shopPolicies() {
        return new KeywordCorpus(List.of(
                new Passage("退款", "签收后7天内可申请退款。质量问题由店铺承担运费。"),
                new Passage("发货", "成都仓在工作日16:00前付款的订单当天发货，默认使用顺丰。")));
    }

    public List<Passage> passages() {
        return passages;
    }

    /** 分数为 0 时不返回资料，避免把不相关的段落塞进提示。并列时保留更早放入的那一段。 */
    public Optional<Passage> best(String question) {
        String normalizedQuestion = normalize(question);
        Passage winner = null;
        int bestScore = 0;
        for (Passage passage : passages) {
            int score = score(normalizedQuestion, normalize(passage.title() + passage.text()));
            if (score > bestScore) {
                bestScore = score;
                winner = passage;
            }
        }
        return Optional.ofNullable(winner);
    }

    static String normalize(String text) {
        if (text == null) return "";
        StringBuilder kept = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c) || "，。！？、；：（）()【】《》,.!?;:\"'".indexOf(c) >= 0) continue;
            kept.append(c);
        }
        return kept.toString();
    }

    static int score(String question, String passage) {
        if (question.isEmpty() || passage.isEmpty()) return 0;
        if (question.length() == 1) return passage.contains(question) ? 1 : 0;
        int overlap = 0;
        for (int i = 0; i < question.length() - 1; i++) {
            if (passage.contains(question.substring(i, i + 2))) overlap++;
        }
        return overlap;
    }
}
