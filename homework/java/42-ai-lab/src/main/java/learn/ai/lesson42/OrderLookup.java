package learn.ai.lesson42;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.util.Map;

/** 订单状态在 Java 里。模型只能请求调用，不能自己编造这份内存表。 */
public final class OrderLookup {
    private final Map<String, String> orders;
    private int calls;

    public OrderLookup(Map<String, String> orders) {
        if (orders == null) throw new IllegalArgumentException("订单表不能为空");
        this.orders = Map.copyOf(orders);
    }

    public static OrderLookup sample() {
        return new OrderLookup(Map.of("1001", "已发货"));
    }

    @Tool("按订单号查询订单状态。参数 orderId 是订单号，例如 1001。")
    public String lookupOrder(@P("orderId") String orderId) {
        calls++;
        if (orderId == null || orderId.isBlank()) return "订单号不能为空。";
        String status = orders.get(orderId.trim());
        if (status == null) return "没有订单 " + orderId.trim() + "。";
        return "订单 " + orderId.trim() + " 的状态是" + status + "。";
    }

    public int calls() {
        return calls;
    }
}
