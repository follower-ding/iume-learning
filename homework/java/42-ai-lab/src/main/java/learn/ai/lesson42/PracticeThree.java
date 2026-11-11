package learn.ai.lesson42;

import java.util.Map;
import java.util.Objects;

/**
 * 练习3 独立：内存表增加 1002=已签收。只调用 Java 方法断言，不走模型、不加 --live。
 * 未知订单是 Map 里没有这个键，不是模型在猜测。
 */
public final class PracticeThree {
    public static void main(String[] args) {
        OrderLookup lookup = new OrderLookup(Map.of("1001", "已发货", "1002", "已签收"));
        String signed = lookup.lookupOrder("1002");
        String missing = lookup.lookupOrder("1003");
        assertEq("订单 1002 的状态是已签收。", signed);
        assertEq("没有订单 1003。", missing);
        System.out.println(signed);
        System.out.println(missing);
        System.out.println("calls=" + lookup.calls());
    }

    private static void assertEq(String expected, String actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("期望：" + expected + "，实际：" + actual);
        }
    }
}
