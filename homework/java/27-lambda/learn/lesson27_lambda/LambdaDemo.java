package learn.lesson27_lambda;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import learn.lesson19_object_identity.Product;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/** 第27课练习副本：匿名类 vs Lambda；四个标准函数式接口；为 Stream 铺路。 */
public final class LambdaDemo {
    private LambdaDemo() {}

    @FunctionalInterface
    interface Calculator {
        int add(int left, int right);
    }

    /** Predicate 判贵、Function 取名；循环让每步可见，原列表不变。 */
    public static List<String> expensiveProductNames(List<Product> products, BigDecimal threshold) {
        Predicate<Product> expensive = product -> product.price().compareTo(threshold) > 0;
        Function<Product, String> name = Product::name;
        List<String> names = new ArrayList<>();
        for (Product product : products) {
            if (expensive.test(product)) {
                names.add(name.apply(product));
            }
        }
        return List.copyOf(names);
    }

    public static void main(String[] args) {
        Calculator oldWay = new Calculator() {
            @Override
            public int add(int left, int right) {
                return left + right;
            }
        };
        Calculator lambda = (left, right) -> left + right;
        System.out.println("匿名类=" + oldWay.add(2, 3));
        System.out.println("Lambda=" + lambda.add(2, 3));

        int threshold = 10; // 捕获的局部变量须有效 final
        Predicate<Integer> expensive = amount -> amount > threshold;
        Function<String, Integer> length = String::length;
        Consumer<String> print = value -> System.out.println("消费=" + value);
        Supplier<String> supply = () -> "订单";

        System.out.println("谓词=" + expensive.test(12));
        System.out.println("转换=" + length.apply("Java"));
        print.accept(supply.get());
    }
}
