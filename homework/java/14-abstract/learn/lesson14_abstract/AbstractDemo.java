package learn.lesson14_abstract;

public class AbstractDemo {
    public static void main(String[] args) {
        // ===== 讲义正文（期望两行）=====
        Shape shape = new Rectangle(3, 4);
        System.out.println(shape.name());
        System.out.println(shape.describe());

        // ===== 练习1：模仿——2×5 矩形，打印面积 =====
        Rectangle r25 = new Rectangle(2, 5);
        System.out.println("练习1 area=" + r25.area());

        // ===== 练习2：修改——边长 -1，观察校验 =====
        try {
            new Rectangle(-1, 5);
            System.out.println("练习2 居然没抛异常？不对劲");
        } catch (IllegalArgumentException e) {
            System.out.println("练习2 catch=" + e.getMessage());
        }

        // ===== 练习3：独立——Square extends Shape =====
        Square sq = new Square(6);
        System.out.println("练习3 name=" + sq.name());
        System.out.println("练习3 describe=" + sq.describe());
        System.out.println("练习3 area=" + sq.area());
    }
}
