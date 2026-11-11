package learn.lesson20_pricing_strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** 百分比折扣策略；费率在 0 到 1 之间，结果按分四舍五入。 */
public final class PercentageDiscountPolicy implements PricingPolicy {
    private final BigDecimal rate;

    public PercentageDiscountPolicy(BigDecimal rate) {
        this.rate = Objects.requireNonNull(rate, "rate must not be null");
        if (rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("rate must be between zero and one");
        }
    }

    @Override
    public BigDecimal apply(BigDecimal subtotal) {
        Objects.requireNonNull(subtotal, "subtotal must not be null");
        return subtotal.multiply(BigDecimal.ONE.subtract(rate)).setScale(2, RoundingMode.HALF_UP);
    }
}
