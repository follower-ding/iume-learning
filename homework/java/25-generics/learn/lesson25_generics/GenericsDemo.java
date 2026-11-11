package learn.lesson25_generics;

import java.util.List;

public final class GenericsDemo {
    private GenericsDemo() {}

    private static final class Box<T> {
        private final T value;

        private Box(T value) {
            this.value = value;
        }

        private T get() {
            return value;
        }
    }

    private static <T> T first(List<T> values) {
        return values.getFirst();
    }

    private static double sum(List<? extends Number> numbers) {
        double result = 0;
        for (Number number : numbers) {
            result += number.doubleValue();
        }
        return result;
    }

    public static void main(String[] args) {
        Box<String> box = new Box<>("Java");
        System.out.println("盒子=" + box.get());
        System.out.println("方法=" + first(List.of(1, 2)));
        System.out.println("上界读取=" + sum(List.of(1, 2)));
        Repository<String, Integer> repository = new InMemoryRepository<>();
        repository.save(1, "订单");
        System.out.println("仓储=" + repository.findById(1).orElseThrow());
    }
}
