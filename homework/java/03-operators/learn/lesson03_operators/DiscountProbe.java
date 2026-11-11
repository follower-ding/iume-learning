package learn.lesson03_operators;

/** 练习2：切换 member 与金额，观察 && 优惠条件 */
public class DiscountProbe {
    public static void main(String[] args) {
        // 原条件：member=true, amount=120 → true
        boolean member = false;
        int amount = 120;
        boolean canDiscount = member && amount >= 100;
        System.out.println("member=false,amount=120 → 可以优惠=" + canDiscount);

        member = true;
        amount = 99;
        canDiscount = member && amount >= 100;
        System.out.println("member=true,amount=99 → 可以优惠=" + canDiscount);

        member = true;
        amount = 120;
        canDiscount = member && amount >= 100;
        System.out.println("member=true,amount=120 → 可以优惠=" + canDiscount);
    }
}
