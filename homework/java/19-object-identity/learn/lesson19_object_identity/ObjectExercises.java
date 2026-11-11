package learn.lesson19_object_identity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/** 讲义末三道练习：先自做再对答案。 */
public final class ObjectExercises {
    private ObjectExercises() {
    }

    /** 不可变键：改 id 只能 new 新实例。 */
    record Key(String id) {
    }

    public static void main(String[] args) {
        // 练习1：改价格后三个相等性；再想改名会怎样
        Product first = new Product("p-1", "Keyboard", new BigDecimal("399.00"));
        Product alias = first;
        Product changedPrice = new Product("p-1", "Keyboard", new BigDecimal("399.01"));
        System.out.println("ex1.alias.sameReference=" + (first == alias)); // true
        System.out.println("ex1.changedPrice.sameReference=" + (first == changedPrice)); // false
        System.out.println("ex1.changedPrice.equalValues=" + first.equals(changedPrice)); // false
        Product changedName = new Product("p-1", "Mouse", new BigDecimal("399.00"));
        System.out.println("ex1.changedName.equalValues=" + first.equals(changedName)); // false

        // 练习2：等值 Product 进 HashSet
        Product sameValues = new Product("p-1", "Keyboard", new BigDecimal("399.00"));
        Set<Product> products = new HashSet<>();
        products.add(first);
        products.add(sameValues);
        System.out.println("ex2.size=" + products.size()); // 1
        System.out.println("ex2.contains.sameValues=" + products.contains(sameValues)); // true
        Product newValue = new Product("p-1", "Keyboard", new BigDecimal("399.01"));
        System.out.println("ex2.contains.newValue=" + products.contains(newValue)); // false

        // 练习3：不可变 Key 替换 BadProductKey
        Key oldKey = new Key("p-1");
        Set<Key> keys = new HashSet<>();
        keys.add(oldKey);
        Key next = new Key("p-2");
        System.out.println("ex3.contains.oldKey=" + keys.contains(oldKey)); // true
        System.out.println("ex3.contains.next.beforeAdd=" + keys.contains(next)); // false
        keys.add(next);
        System.out.println("ex3.contains.next.afterAdd=" + keys.contains(next)); // true
        System.out.println("ex3.size=" + keys.size()); // 2

        // 额外：BigDecimal scale 陷阱
        BigDecimal a = new BigDecimal("399.0");
        BigDecimal b = new BigDecimal("399.00");
        System.out.println("ex.bd.equals.scale=" + a.equals(b)); // false（还比 scale）
        System.out.println("ex.bd.compareTo=" + (a.compareTo(b) == 0)); // true（数值相等）
    }
}
