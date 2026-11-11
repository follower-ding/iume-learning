package learn.lesson13_inheritance;

public class Cat extends Animal {
    public Cat(String name) {
        super(name);
    }

    @Override
    public String speak() {
        return "喵喵";
    }
}
