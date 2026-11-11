package learn.lesson03_operators;

public class OperatorsDemo {
    public static void main(String[] args) {
        int apples = 7;
        int people = 3;

        System.out.println("每人得到=" + apples / people);
        System.out.println("剩余=" + apples % people);
        System.out.println("够分吗=" + (apples >= people));

        boolean member = true;
        int amount = 120;
        boolean canDiscount = member && amount >= 100;
        System.out.println("可以优惠=" + canDiscount);

        int count = 0;
        count += 2;
        count++;
        System.out.println("数量=" + count);
    }
}
