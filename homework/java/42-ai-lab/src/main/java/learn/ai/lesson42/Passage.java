package learn.ai.lesson42;

/** 一小段可被检索的资料。title 用来解释命中了哪一条，text 才放进提示。 */
public record Passage(String title, String text) {
    public Passage {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("资料标题不能为空");
        if (text == null || text.isBlank()) throw new IllegalArgumentException("资料正文不能为空");
    }
}
