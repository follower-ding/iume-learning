package learn.lesson23_map;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

public final class MapDemo {
    private MapDemo() {}

    private static final class InitialStock implements Function<String, Integer> {
        @Override public Integer apply(String productId) {
            return 1; // 缺失时生成初始库存；第27课会把这写成 Lambda。
        }
    }

    public static Map<String, Integer> replaceStock() {
        Map<String, Integer> stock = new HashMap<>();
        stock.put("pen", 5);
        stock.put("pen", 8); // 同一键再次放入会替换旧值。
        return stock;
    }

    public static Map<String, Integer> stockWithUnknownQuantity() {
        Map<String, Integer> stock = new HashMap<>();
        stock.put("pen", null); // HashMap 允许 null 值；get 单独无法分辨缺失。
        return stock;
    }

    public static void main(String[] args) {
        Map<String, Integer> stock = new LinkedHashMap<>();
        stock.put("pen", 5);
        stock.put("book", 2);
        System.out.println("读取=" + stock.get("pen"));
        System.out.println("默认值=" + stock.getOrDefault("missing", 0));
        System.out.println("存在=" + stock.containsKey("book"));
        stock.put("pen", 8);
        stock.computeIfAbsent("eraser", new InitialStock());
        for (Map.Entry<String, Integer> entry : stock.entrySet())
            System.out.println("条目=" + entry.getKey() + ":" + entry.getValue());
        stock.remove("book");
        System.out.println("删除后=" + stock);
        System.out.println("按键排序=" + new TreeMap<>(stock));
        System.out.println("映射空值存在=" + stockWithUnknownQuantity().containsKey("pen"));
    }
}
