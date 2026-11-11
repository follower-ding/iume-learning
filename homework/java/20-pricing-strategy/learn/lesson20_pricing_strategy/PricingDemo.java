package learn.lesson20_pricing_strategy;

import java.math.BigDecimal;

public final class PricingDemo {
    private PricingDemo() {}
    public static void main(String[] args) {
        BigDecimal subtotal = new BigDecimal("100.00");
        PricingPolicy normal = new FullPricePolicy(); // 接口变量可装不同实现。
        PricingPolicy discount = new PercentageDiscountPolicy(new BigDecimal("0.10"));
        System.out.println("原价=" + normal.apply(subtotal));
        System.out.println("九折=" + discount.apply(subtotal));
    }
}
