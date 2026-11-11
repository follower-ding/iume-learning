package learn.lesson20_pricing_strategy;

import java.math.BigDecimal;

/** 计价规则的共同入口；订单依赖接口即可替换具体折扣算法。 */
public interface PricingPolicy {
    BigDecimal apply(BigDecimal subtotal);
}
