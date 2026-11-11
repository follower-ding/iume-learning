package learn.lesson12_static;

public class Visitor {
    public static final String SITE_NAME = "学习站";
    private static int createdCount;
    private String name;

    public Visitor(String name) {
        this.name = name;
        createdCount++;
    }

    public String name() {
        return name;
    }

    public static int createdCount() {
        // 静态方法没有 this，只能直接访问静态成员。
        return createdCount;
    }

    // 练习3：实例方法可同时用静态常量与当前对象字段
    public String label() {
        return SITE_NAME + ":" + name;
    }
}
