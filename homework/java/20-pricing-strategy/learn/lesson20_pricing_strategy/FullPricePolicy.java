package learn.lesson20_pricing_strategy;

import java.math.BigDecimal;
import java.util.Objects;

/** 原价策略：直接返回非空小计，不进行折扣。 */
public final class FullPricePolicy implements PricingPolicy {
    @Override
    public BigDecimal apply(BigDecimal subtotal) {
        return Objects.requireNonNull(subtotal, "subtotal must not be null");
    }
}
