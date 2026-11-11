package learn.lesson13_inheritance;

public class InheritanceDemo {
    public static void main(String[] args) {
        // ===== 讲义正文（期望两行）=====
        Dog dog = new Dog("旺财");
        System.out.println(dog.eat());   // 继承来的 eat，父类 private name 也能拼出来
        System.out.println(dog.speak()); // 重写后的叫声

        // ===== 练习1：模仿——Dog 继承 eat() 并打印（上面已印「旺财在吃饭」）=====
        System.out.println("练习1 再确认 eat=" + dog.eat());

        // ===== 练习2：修改——叫声改为「汪」，第二行随之变 =====
        YapDog yap = new YapDog("旺财");
        System.out.println("练习2 改叫声 eat=" + yap.eat());
        System.out.println("练习2 改叫声 speak=" + yap.speak());

        // ===== 练习3：独立——Cat extends Animal，speak → 喵喵 =====
        Cat cat = new Cat("咪咪");
        System.out.println("练习3 cat eat=" + cat.eat());
        System.out.println("练习3 cat speak=" + cat.speak());
    }
}
