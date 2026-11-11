package learn.lesson13_inheritance;

public class Dog extends Animal {
    public Dog(String name) {
        // super 必须是子类构造的首行：先让父类把 name 装好。
        super(name);
    }

    @Override
    public String speak() {
        return "汪汪";
    }
}
