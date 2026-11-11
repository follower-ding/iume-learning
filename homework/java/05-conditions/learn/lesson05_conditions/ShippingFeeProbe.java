package learn.lesson05_conditions;

/** 练习3：满99免运费，否则运费10；负金额拒绝 */
public class ShippingFeeProbe {
    public static int shippingFee(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("金额不能为负数");
        }
        if (amount >= 99) {
            return 0;
        }
        return 10;
    }

    public static void main(String[] args) {
        System.out.println("98运费=" + shippingFee(98));
        System.out.println("99运费=" + shippingFee(99));
        try {
            System.out.println("负数=" + shippingFee(-1));
        } catch (IllegalArgumentException e) {
            System.out.println("负数拒绝=" + e.getMessage());
        }
    }
}
