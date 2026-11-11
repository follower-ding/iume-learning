package learn.lesson28_streams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class StreamsDemo {
    private StreamsDemo() {}
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
        List<OrderView> orders = new ArrayList<>(List.of(
                new OrderView("A", 90, true), new OrderView("B", 20, false),
                new OrderView("C", 50, true)));
        // 先用循环表达相同的筛选；Stream 版本在方法中复用。
        List<String> paidLoop = new ArrayList<>();
        for (OrderView order : orders) if (order.paid()) paidLoop.add(order.id());
        System.out.println("循环筛选=" + paidLoop);
        System.out.println("流水线排序=" + paidIdsByAmount(orders));
        System.out.println("已支付总额=" + paidTotal(orders));
        System.out.println("源数量=" + orders.size());
    }
}
