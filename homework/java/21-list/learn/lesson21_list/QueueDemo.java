package learn.lesson21_list;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public final class QueueDemo {
    private QueueDemo() {}

    public static List<String> serve(List<String> arriving) {
        Deque<String> queue = new ArrayDeque<>();
        for (String order : arriving) queue.offer(Objects.requireNonNull(order));
        List<String> served = new ArrayList<>();
        while (!queue.isEmpty()) served.add(queue.poll()); // FIFO：先来先处理。
        return List.copyOf(served);
    }

    public static String pollEmpty() {
        return new ArrayDeque<String>().poll(); // 空队列返回 null。
    }

    public static void main(String[] args) {
        System.out.println("出队=" + serve(List.of("订单A", "订单B")));
        System.out.println("空队列=" + pollEmpty());
        Deque<String> stack = new ArrayDeque<>();
        stack.push("先放");
        stack.push("后放");
        System.out.println("出栈=" + stack.pop()); // LIFO：后放先取。
    }
}
