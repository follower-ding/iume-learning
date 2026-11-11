package learn.lesson16_polymorphism;

// 练习3：独立——Bird extends Animal，speak → 啾啾
public class Bird extends Animal {
    @Override
    public String speak() {
        return "啾啾";
    }
}
