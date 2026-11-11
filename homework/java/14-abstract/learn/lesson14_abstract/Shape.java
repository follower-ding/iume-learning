package learn.lesson14_abstract;

public abstract class Shape {
    private String name;

    protected Shape(String name) {
        this.name = name;
    }

    // 抽象方法：只写签名，具体公式交给子类
    public abstract long area();

    public String describe() {
        return "面积=" + area();
    }

    public String name() {
        return name;
    }
}
