package learn.lesson26_exceptions;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 第26课三级练习（自敲迷你版，不改讲义 capstone）：
 * 1) 数量边界：0/负数 → IllegalArgumentException；1 合法且小计=单价
 * 2) 取消规则：只允许 CREATED → CANCELLED；重复取消抛 InvalidOrderStateException
 * 3) 仓储故障包装 cause；业务 ProductNotFoundException 原样传播
 */
public final class ExceptionsExercises {
    private ExceptionsExercises() {}

    /** 迷你商品：只要 id 与单价。 */
    record MiniProduct(String id, BigDecimal price) {
        MiniProduct {
            Objects.requireNonNull(id);
            Objects.requireNonNull(price);
        }
    }

    /** 练习1：数量至少为 1，否则调用错误。 */
    static final class MiniLine {
        private final MiniProduct product;
        private final int quantity;

        MiniLine(MiniProduct product, int quantity) {
            this.product = Objects.requireNonNull(product, "product must not be null");
            if (quantity < 1) {
                throw new IllegalArgumentException("quantity must be at least one");
            }
            this.quantity = quantity;
        }

        BigDecimal subtotal() {
            return product.price().multiply(BigDecimal.valueOf(quantity));
        }
    }

    enum MiniStatus { CREATED, CONFIRMED, CANCELLED }

    /** 练习2：确认 / 取消状态机。 */
    static final class MiniOrder {
        private final String id;
        private MiniStatus status = MiniStatus.CREATED;

        MiniOrder(String id) {
            this.id = Objects.requireNonNull(id);
        }

        MiniStatus status() {
            return status;
        }

        void confirm() {
            if (status != MiniStatus.CREATED) {
                throw new InvalidOrderStateException(
                        "cannot confirm order " + id + " from state " + status);
            }
            status = MiniStatus.CONFIRMED;
        }

        void cancel() {
            if (status != MiniStatus.CREATED) {
                throw new InvalidOrderStateException(
                        "cannot cancel order " + id + " from state " + status);
            }
            status = MiniStatus.CANCELLED;
        }
    }

    /** 练习3：查商品；缺货抛业务异常，其他运行时故障包装 cause。 */
    static final class MiniCatalog {
        private final RuntimeException failureOrNull;
        private final boolean missingAsBusiness;

        MiniCatalog(RuntimeException failureOrNull, boolean missingAsBusiness) {
            this.failureOrNull = failureOrNull;
            this.missingAsBusiness = missingAsBusiness;
        }

        MiniProduct load(String id) {
            if (failureOrNull != null) {
                if (missingAsBusiness && failureOrNull instanceof ProductNotFoundException) {
                    throw failureOrNull; // 业务异常原样传播
                }
                if (failureOrNull instanceof ProductNotFoundException) {
                    throw failureOrNull;
                }
                throw new IllegalStateException("failed to load product: " + id, failureOrNull);
            }
            throw new ProductNotFoundException("product not found: " + id);
        }
    }

    public static void main(String[] args) {
        MiniProduct keyboard = new MiniProduct("p-1", new BigDecimal("100.00"));

        // 练习1：0 / 负数非法；1 合法且小计=单价
        try {
            new MiniLine(keyboard, 0);
            System.out.println("练习1-qty0=意外通过");
        } catch (IllegalArgumentException e) {
            System.out.println("练习1-qty0=" + e.getMessage());
        }
        try {
            new MiniLine(keyboard, -1);
            System.out.println("练习1-qtyNeg=意外通过");
        } catch (IllegalArgumentException e) {
            System.out.println("练习1-qtyNeg=" + e.getMessage());
        }
        MiniLine ok = new MiniLine(keyboard, 1);
        System.out.println("练习1-qty1-subtotal=" + ok.subtotal()); // 100.00

        // 练习2：首次取消成功；重复取消失败
        MiniOrder order = new MiniOrder("o-1");
        order.cancel();
        System.out.println("练习2-afterCancel=" + order.status()); // CANCELLED
        try {
            order.cancel();
            System.out.println("练习2-reCancel=意外通过");
        } catch (InvalidOrderStateException e) {
            System.out.println("练习2-reCancel=" + e.getMessage());
        }

        // 练习3a：仓储 offline → 包装消息含商品 ID，cause 同一引用
        RuntimeException root = new IllegalStateException("offline");
        MiniCatalog offline = new MiniCatalog(root, false);
        try {
            offline.load("p-1");
        } catch (IllegalStateException wrapped) {
            System.out.println("练习3-wrap-msg=" + wrapped.getMessage());
            System.out.println("练习3-wrap-sameCause=" + (wrapped.getCause() == root));
        }

        // 练习3b：仓储直接抛 ProductNotFoundException → 原样传播
        ProductNotFoundException missing = new ProductNotFoundException("catalog unavailable");
        MiniCatalog biz = new MiniCatalog(missing, true);
        try {
            biz.load("p-1");
        } catch (ProductNotFoundException thrown) {
            System.out.println("练习3-biz-same=" + (thrown == missing));
            System.out.println("练习3-biz-msg=" + thrown.getMessage());
        }
    }
}
