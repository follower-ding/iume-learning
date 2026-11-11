package learn.lesson16_polymorphism;

public class PolymorphismDemo {
    public static void main(String[] args) {
        // ===== 讲义正文（期望两行：汪汪 / 喵喵）=====
        // 左侧 Animal 限定可调用方法；右侧实际对象决定 speak 的实现。
        Animal first = new Dog();
        Animal second = new Cat();
        System.out.println(first.speak());
        System.out.println(second.speak());

        // ===== 练习1：模仿——再创建一个 Dog 的 Animal 引用并 speak =====
        Animal third = new Dog();
        System.out.println("练习1 " + third.speak());

        // ===== 练习2：修改——交换赋值，预测：先喵喵后汪汪 =====
        Animal swappedFirst = new Cat();
        Animal swappedSecond = new Dog();
        System.out.println("练习2a " + swappedFirst.speak());
        System.out.println("练习2b " + swappedSecond.speak());

        // ===== 练习3：独立——Bird =====
        Animal bird = new Bird();
        System.out.println("练习3 " + bird.speak());
    }
}
