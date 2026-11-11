package learn.lesson20_pricing_strategy;

import java.math.BigDecimal;

/** 讲义末三道练习：固定减额 / 同小计换策略 / 不能 new 接口。 */
public final class PricingExercises {
    private PricingExercises() {
    }

    public static void main(String[] args) {
        BigDecimal subtotal = new BigDecimal("100.00");

        // 练习1：固定减额策略（经 PricingPolicy 调用）
        PricingPolicy fixed5 = new FixedDiscountPolicy(new BigDecimal("5.00"));
        PricingPolicy fixedOver = new FixedDiscountPolicy(new BigDecimal("5.00"));
        System.out.println("ex1.100-5=" + fixed5.apply(subtotal)); // 95.00
        System.out.println("ex1.3-5=" + fixedOver.apply(new BigDecimal("3.00"))); // 0.00（减额超小计 → 0）

        // 练习2：同一小计依次交给原价和九折，无需 if 判类型
        PricingPolicy first = new FullPricePolicy();
        PricingPolicy second = new PercentageDiscountPolicy(new BigDecimal("0.10"));
        System.out.println("ex2.full=" + first.apply(subtotal)); // 100.00（沿用输入 scale）
        System.out.println("ex2.discount=" + second.apply(subtotal)); // 90.00（实现里 setScale(2)）
        // 换策略只需换实现对象；调用方始终 policy.apply(subtotal)，没有 instanceof / switch。

        // 练习3：接口不能 new（见同目录 CompileFailNote.java 注释）；正确写法：
        PricingPolicy policy = new FullPricePolicy(); // 左边接口类型，右边实现对象
        System.out.println("ex3.viaInterface=" + policy.apply(subtotal)); // 100.00
        // PricingPolicy bad = new PricingPolicy(); // 编译失败：接口不能直接实例化
    }
}
