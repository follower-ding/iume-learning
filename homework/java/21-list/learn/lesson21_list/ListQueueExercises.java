package learn.lesson21_list;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 第21课三级练习（先预测再运行）：
 * 1) goods 追加「橡皮」后的 size 与遍历
 * 2) remove(0) 与 remove(Integer.valueOf(1)) 对数字列表的影响
 * 3) ArrayDeque 三张取号票 FIFO，第四次 poll 为 null
 */
public final class ListQueueExercises {
    private ListQueueExercises() {}

    public static void main(String[] args) {
        // 练习1：追加橡皮（放在遍历之前）
        List<String> goods = new ArrayList<>();
        goods.add("笔");
        goods.add("本子");
        goods.add("橡皮");
        System.out.println("练习1数量=" + goods.size());
        for (String g : goods) System.out.println("练习1遍历=" + g);

        // 练习2：从 [1,2,3] 出发
        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3));
        numbers.remove(0); // 删位置 0 → [2,3]
        System.out.println("练习2-remove0=" + numbers);
        numbers.remove(Integer.valueOf(1)); // 值 1 已不在，列表不变
        System.out.println("练习2-remove值1=" + numbers);

        // 练习3：三张取号票
        Deque<String> tickets = new ArrayDeque<>();
        tickets.offer("A");
        tickets.offer("B");
        tickets.offer("C");
        System.out.println("练习3-1=" + tickets.poll());
        System.out.println("练习3-2=" + tickets.poll());
        System.out.println("练习3-3=" + tickets.poll());
        System.out.println("练习3-空=" + tickets.poll());
    }
}
