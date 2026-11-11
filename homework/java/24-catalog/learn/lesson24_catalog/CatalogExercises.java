package learn.lesson24_catalog;

import learn.lesson19_object_identity.Product;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 第24课三级练习（先预测再运行）：
 * 1) 按名称前缀过滤 → List 快照
 * 2) 整元价格分组统计 → {0=1, 1=2}
 * 3) HashSet vs LinkedHashSet 名称去重与遍历顺序
 */
public final class CatalogExercises {
    private CatalogExercises() {}

    public static void main(String[] args) {
        // 练习1：前缀过滤；空前缀匹配全部；大小写敏感；List 保目录顺序且不可变
        ProductCatalog catalog1 = new ProductCatalog();
        catalog1.add(new Product("book", "Book", new BigDecimal("12.90")));
        catalog1.add(new Product("pen", "Pen", new BigDecimal("1.50")));
        catalog1.add(new Product("bag", "Bag", new BigDecimal("30.00")));
        catalog1.add(new Product("box", "Box", new BigDecimal("5.00")));
        List<Product> withB = catalog1.findByNamePrefix("B");
        List<Product> emptyPrefix = catalog1.findByNamePrefix("");
        List<Product> lowerB = catalog1.findByNamePrefix("b");
        System.out.println("练习1-前缀B=" + ids(withB));
        System.out.println("练习1-空前缀size=" + emptyPrefix.size());
        System.out.println("练习1-前缀b(小写)=" + ids(lowerB));
        try {
            withB.clear();
            System.out.println("练习1-快照可改=意外");
        } catch (UnsupportedOperationException ex) {
            System.out.println("练习1-快照不可变=true");
        }

        // 练习2：0.99→0，1.00 与 1.99→1 → {0=1, 1=2}
        ProductCatalog catalog2 = new ProductCatalog();
        catalog2.add(new Product("a", "A", new BigDecimal("0.99")));
        catalog2.add(new Product("b", "B", new BigDecimal("1.00")));
        catalog2.add(new Product("c", "C", new BigDecimal("1.99")));
        Map<BigDecimal, Integer> counts = catalog2.countByWholePrice();
        System.out.println("练习2-counts=" + counts);

        // 练习3：HashSet 去重但不保序；LinkedHashSet 去重且保首次插入序
        List<String> names = List.of("Book", "Pen", "Book", "Card", "Pen");
        Set<String> hash = new HashSet<>();
        Set<String> linked = new LinkedHashSet<>();
        for (String name : names) {
            hash.add(name);
            linked.add(name);
        }
        System.out.println("练习3-HashSet=" + hash);
        System.out.println("练习3-LinkedHashSet=" + linked);
        System.out.println("练习3-业务契约顺序=LinkedHashSet");
    }

    private static String ids(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        for (Product product : products) {
            if (!sb.isEmpty()) sb.append(",");
            sb.append(product.id());
        }
        return sb.toString();
    }
}
