package learn.lesson10_constructors;

public class Book {
    private String title;

    public Book() {
        // this(...) 必须是构造方法的第一条语句。
        // 练习2：默认标题改成「待录入」
        this("待录入");
    }

    public Book(String title) {
        // this.title 是当前对象的字段；右边 title 是参数。
        this.title = title;
    }

    // 练习3：带版次的构造，标题里拼上「第N版」
    public Book(String title, int edition) {
        this(title + " 第" + edition + "版");
    }

    public String title() {
        return title;
    }
}
