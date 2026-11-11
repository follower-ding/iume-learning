package learn.lesson24_catalog;

import learn.lesson19_object_identity.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/** 用普通循环展示目录的查找、排序和分组，Stream 留到后续课程。 */
public final class CollectionsDemo {
    private CollectionsDemo() {}
    public static void main(String[] args) {
        ProductCatalog catalog = new ProductCatalog();
        catalog.add(new Product("book", "Book", new BigDecimal("12.90")));
        catalog.add(new Product("pen", "Pen", new BigDecimal("1.50")));
        catalog.add(new Product("card", "Card", new BigDecimal("12.10")));
        System.out.println("lookup book: " + catalog.findById("book").orElseThrow().name());
        StringBuilder prices = new StringBuilder();
        for (Product product : catalog.sortedByPrice()) {
            if (!prices.isEmpty()) prices.append(", ");
            prices.append(product.id()).append("=").append(product.price());
        }
        System.out.println("sorted prices: " + prices);
        for (Map.Entry<BigDecimal, List<Product>> group : catalog.groupByWholePrice().entrySet()) {
            StringBuilder ids = new StringBuilder();
            for (Product product : group.getValue()) {
                if (!ids.isEmpty()) ids.append(", ");
                ids.append(product.id());
            }
            System.out.println("whole price " + group.getKey() + ": " + ids);
        }
    }
}
