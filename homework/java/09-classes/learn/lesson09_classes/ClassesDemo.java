package learn.lesson09_classes;

public class ClassesDemo {
    public static void main(String[] args) {
        // new 创建两个独立对象，变量保存各自的引用。
        Student first = new Student("小明");
        Student second = new Student("小兰"); // 练习2：小红改小兰
        System.out.println(first.introduce());
        System.out.println(second.introduce());

        // 练习1：再创建名叫小华的学生并打印介绍
        Student third = new Student("小华");
        System.out.println(third.introduce());

        // 练习3：greet
        System.out.println(first.greet("小红"));
    }
}
