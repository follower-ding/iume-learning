package learn.lesson12_static;

public class StaticDemo {
    public static void main(String[] args) {
        // ===== 讲义正文（期望三行）=====
        Visitor first = new Visitor("甲");
        Visitor second = new Visitor("乙");
        System.out.println(first.name() + "/" + second.name());
        System.out.println("创建数=" + Visitor.createdCount());
        System.out.println(Visitor.SITE_NAME);

        // ===== 练习1：模仿——再创建第三个 Visitor，计数应为 3 =====
        Visitor third = new Visitor("丙");
        System.out.println("练习1第三访客=" + third.name() + " 创建数=" + Visitor.createdCount());

        // ===== 练习2：修改——交换甲乙创建顺序，姓名各自独立，静态计数继续累加 =====
        // 说明：类级 createdCount 不会因交换顺序而重置；姓名随实例走。
        // 这里新建一对交换顺序的访客，观察姓名 vs 计数。
        Visitor swapFirst = new Visitor("乙");
        Visitor swapSecond = new Visitor("甲");
        System.out.println("练习2交换顺序=" + swapFirst.name() + "/" + swapSecond.name()
                + " 创建数=" + Visitor.createdCount());

        // ===== 练习3：独立——label() 返回 站点名:姓名 =====
        System.out.println("练习3 label first=" + first.label());
        System.out.println("练习3 label third=" + third.label());
    }
}
