package learn.lesson21_list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class ListDemo {
    private ListDemo() {}

    public static List<Integer> editQuantities(List<Integer> input) {
        // 复制输入：示例方法不改调用者持有的列表。
        List<Integer> quantities = new ArrayList<>(input);
        if (quantities.size() > 1) quantities.remove(1); // 1 是位置，不是 Integer 值。
        return List.copyOf(quantities);
    }

    public static void main(String[] args) {
        List<String> goods = new ArrayList<>();
        goods.add("笔");
        goods.add("书");
        System.out.println("读取=" + goods.get(0));
        goods.set(1, "本子");
        System.out.println("数量=" + goods.size());
        for (String good : goods) System.out.println("遍历=" + good);
        // 遍历时删除，使用迭代器自己的 remove，而非直接调用 goods.remove。
        Iterator<String> iterator = goods.iterator();
        while (iterator.hasNext()) if (iterator.next().equals("笔")) iterator.remove();
        System.out.println("删除后=" + goods);
        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3));
        numbers.remove(1); // 删除位置 1 的元素 2。
        numbers.remove(Integer.valueOf(3)); // 删除值 3。
        System.out.println("数字=" + numbers);
        List<String> linked = new LinkedList<>(goods);
        linked.add(0, "封面");
        System.out.println("链表=" + linked);
    }
}
