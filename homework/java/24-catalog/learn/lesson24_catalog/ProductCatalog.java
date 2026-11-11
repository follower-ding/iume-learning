package learn.lesson24_catalog;

import learn.lesson19_object_identity.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

/** 按商品 ID 建索引，并提供价格排序和按整数价格段分组的快照。 */
public final class ProductCatalog {
    // LinkedHashMap：按 ID 查找，同时保留加入顺序。
    private final Map<String, Product> products = new LinkedHashMap<>();

    public void add(Product product) {
        Objects.requireNonNull(product, "product must not be null");
        products.put(product.id(), product);
    }

    public Optional<Product> findById(String id) {
        Objects.requireNonNull(id, "id must not be null");
        return Optional.ofNullable(products.get(id));
    }

    public List<Product> sortedByPrice() {
        // 先复制再排序，避免改变目录内部顺序。
        List<Product> sorted = new ArrayList<>(products.values());
        sorted.sort(Comparator.comparing(Product::price));
        return List.copyOf(sorted);
    }

    public Map<BigDecimal, List<Product>> groupByWholePrice() {
        Map<BigDecimal, List<Product>> buckets = new TreeMap<>();
        for (Product product : products.values()) {
            BigDecimal wholePrice = product.price().setScale(0, RoundingMode.DOWN);
            buckets.computeIfAbsent(wholePrice, ignored -> new ArrayList<>()).add(product);
        }
        Map<BigDecimal, List<Product>> snapshot = new LinkedHashMap<>();
        buckets.forEach((price, members) -> snapshot.put(price, List.copyOf(members)));
        return Collections.unmodifiableMap(snapshot);
    }

    /** 练习1：按名称前缀过滤；空前缀匹配全部；返回不可变 List 快照（保留目录顺序）。 */
    public List<Product> findByNamePrefix(String prefix) {
        Objects.requireNonNull(prefix, "prefix must not be null");
        List<Product> matches = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.name().startsWith(prefix)) {
                matches.add(product);
            }
        }
        return List.copyOf(matches);
    }

    /** 练习2：按整元价格分组统计数量；键升序；不可变快照。 */
    public Map<BigDecimal, Integer> countByWholePrice() {
        Map<BigDecimal, Integer> counts = new TreeMap<>();
        for (Map.Entry<BigDecimal, List<Product>> group : groupByWholePrice().entrySet()) {
            counts.put(group.getKey(), group.getValue().size());
        }
        return Collections.unmodifiableMap(counts);
    }
}
