package learn.lesson19_object_identity;

import java.math.BigDecimal;
import java.util.Objects;

/** 商品值对象；record 默认比较所有组件，构造时统一拒绝非法字段。 */
public record Product(String id, String name, BigDecimal price) {
    public Product {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(price, "price must not be null");
        if (id.isBlank()) throw new IllegalArgumentException("id must not be blank");
        if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        if (price.signum() < 0) throw new IllegalArgumentException("price must not be negative");
    }
}
