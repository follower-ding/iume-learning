package learn.lesson20_pricing_strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 练习1：固定减额策略。减额非负；减额超过小计时落到 0（不允许负应付）。
 */
public final class FixedDiscountPolicy implements PricingPolicy {
    private final BigDecimal discount;

    public FixedDiscountPolicy(BigDecimal discount) {
        this.discount = Objects.requireNonNull(discount, "discount must not be null");
        if (discount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("discount must not be negative");
        }
    }

    @Override
    public BigDecimal apply(BigDecimal subtotal) {
        Objects.requireNonNull(subtotal, "subtotal must not be null");
        return subtotal.subtract(discount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
}
