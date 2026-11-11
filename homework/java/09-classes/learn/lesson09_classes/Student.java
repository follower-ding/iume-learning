package learn.lesson09_classes;

public class Student {
    String name;

    public Student(String initialName) {
        name = initialName;
    }

    public String introduce() {
        return "我叫" + name;
    }

    // 练习3：打招呼
    public String greet(String other) {
        return "你好，" + other + "，我叫" + name;
    }
}
