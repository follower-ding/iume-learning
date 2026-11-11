package learn.lesson23_map;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * 第23课三级练习（先预测再运行）：
 * 1) 再 put("pen", 9)，预测 size 与 get("pen")
 * 2) computeIfAbsent("eraser", ...) 连续两次，记录计算次数
 * 3) TreeMap 汇总 A、B、A 出现次数并按键输出
 */
public final class MapExercises {
    private MapExercises() {}

    private static final class CountingInitial implements Function<String, Integer> {
        private final AtomicInteger calls;

        CountingInitial(AtomicInteger calls) {
            this.calls = calls;
        }

        @Override public Integer apply(String key) {
            calls.incrementAndGet();
            return 1;
        }
    }

    public static void main(String[] args) {
        // 练习1：覆盖不会增加 size；get 得 9
        Map<String, Integer> stock = new TreeMap<>();
        stock.put("pen", 5);
        stock.put("pen", 8);
        stock.put("pen", 9);
        System.out.println("练习1-size=" + stock.size());
        System.out.println("练习1-getPen=" + stock.get("pen"));

        // 练习2：第一次缺失会计算；第二次已存在不再计算
        AtomicInteger calls = new AtomicInteger(0);
        Map<String, Integer> stock2 = new TreeMap<>();
        stock2.computeIfAbsent("eraser", new CountingInitial(calls));
        stock2.computeIfAbsent("eraser", new CountingInitial(calls));
        System.out.println("练习2-计算次数=" + calls.get());
        System.out.println("练习2-eraser=" + stock2.get("eraser"));

        // 练习3：A、B、A → {A=2, B=1}
        Map<String, Integer> counts = new TreeMap<>();
        for (String id : List.of("A", "B", "A")) {
            counts.put(id, counts.getOrDefault(id, 0) + 1);
        }
        System.out.println("练习3-counts=" + counts);
    }
}
