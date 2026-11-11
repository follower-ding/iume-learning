package learn.lesson13_inheritance;

/** 练习2：把叫声改成「汪」，观察第二行变化（演示用，不进讲义仓）。 */
public class YapDog extends Animal {
    public YapDog(String name) {
        super(name);
    }

    @Override
    public String speak() {
        return "汪";
    }
}
