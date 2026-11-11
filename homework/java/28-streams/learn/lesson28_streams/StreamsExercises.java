package learn.lesson28_streams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 第28课三级练习（自敲，先预测再跑）：
 * 1) 加订单 D(10,true) → 排序与总额
 * 2) 把 C 改为未支付 → 两筛选结果；源数量仍 3
 * 3) 重复商品类别：循环版+Stream 版，只保留以 A 开头的唯一类别并排序
 */
public final class StreamsExercises {
    private StreamsExercises() {}

    public record OrderView(String id, int amount, boolean paid) {}

    public static List<String> paidIdsByAmount(List<OrderView> source) {
        return source.stream().filter(OrderView::paid)
                .sorted(Comparator.comparingInt(OrderView::amount))
                .map(OrderView::id).distinct().toList();
    }

    public static int paidTotal(List<OrderView> source) {
        return source.stream().filter(OrderView::paid).map(OrderView::amount)
                .reduce(0, Integer::sum);
    }

    public static void main(String[] args) {
        // 练习1：加 D(10, true) —— 预测排序 [D, C, A]，总额 150
        List<OrderView> orders1 = new ArrayList<>(List.of(
                new OrderView("A", 90, true), new OrderView("B", 20, false),
                new OrderView("C", 50, true), new OrderView("D", 10, true)));
        System.out.println("练习1流水线排序=" + paidIdsByAmount(orders1));
        System.out.println("练习1已支付总额=" + paidTotal(orders1));

        // 练习2：C 改为未支付 —— 预测循环/Stream 都是 [A]，总额 90，源数量 3
        List<OrderView> orders2 = new ArrayList<>(List.of(
                new OrderView("A", 90, true), new OrderView("B", 20, false),
                new OrderView("C", 50, false)));
        List<String> paidLoop = new ArrayList<>();
        for (OrderView order : orders2) if (order.paid()) paidLoop.add(order.id());
        System.out.println("练习2循环筛选=" + paidLoop);
        System.out.println("练习2流水线排序=" + paidIdsByAmount(orders2));
        System.out.println("练习2已支付总额=" + paidTotal(orders2));
        System.out.println("练习2源数量=" + orders2.size());

        // 练习3：重复类别，只保留以 A 开头的唯一值并排序
        List<String> categories = List.of("Apple", "Banana", "Apricot", "Apple", "Avocado", "Berry");
        // 循环版：Set 去重，再筛 A 开头，再排序
        Set<String> seen = new HashSet<>();
        List<String> loopResult = new ArrayList<>();
        for (String cat : categories) {
            if (cat.startsWith("A") && seen.add(cat)) {
                loopResult.add(cat);
            }
        }
        loopResult.sort(Comparator.naturalOrder());
        // Stream 版
        List<String> streamResult = categories.stream()
                .filter(x -> x.startsWith("A"))
                .distinct()
                .sorted()
                .toList();
        System.out.println("练习3循环版=" + loopResult);
        System.out.println("练习3Stream版=" + streamResult);
    }
}
