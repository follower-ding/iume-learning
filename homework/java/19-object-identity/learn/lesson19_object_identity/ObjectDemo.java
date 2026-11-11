package learn.lesson19_object_identity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/** 先比较普通类对象的引用，再观察 record 的值相等性和可变哈希键误用。 */
public final class ObjectDemo {
    private ObjectDemo() {
    }

    private static final class PlainItem {
        private final String name;
        private PlainItem(String name) { this.name = name; }
        public String name() { return name; }
    }

    public static void main(String[] args) {
        PlainItem one = new PlainItem("书");
        PlainItem aliasItem = one; // 赋值复制引用，不创建对象。
        PlainItem another = new PlainItem("书");
        System.out.println("普通类同一引用=" + (one == aliasItem));
        System.out.println("普通类独立对象=" + (one == another));
        System.out.println("普通类相同字段=" + one.name().equals(another.name()));
        // 先掌握普通类，再观察 record 自动生成的值相等性。
        Product first = new Product("p-1", "Keyboard", new BigDecimal("399.00"));
        Product alias = first;
        Product sameValues = new Product("p-1", "Keyboard", new BigDecimal("399.00"));

        System.out.println("alias.sameReference=" + (first == alias));
        System.out.println("separate.sameReference=" + (first == sameValues));
        System.out.println("separate.equalValues=" + first.equals(sameValues));

        BadProductKey key = new BadProductKey("p-1");
        Set<BadProductKey> keys = new HashSet<>();
        keys.add(key);
        System.out.println("mutableKey.containsBefore=" + keys.contains(key));
        key.setId("p-2");
        System.out.println("mutableKey.containsAfter=" + keys.contains(key));
    }
}
