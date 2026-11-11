package learn.lesson25_generics;

import java.util.ArrayList;
import java.util.List;

/**
 * 第25课三级练习（先预测再运行）：
 * 1) PECS copy：List<? extends T> 读 → List<? super T> 写
 * 2) 上界 <T extends Number> 限制来源必须是 Number 子类型
 * 3) Repository 插入/替换/删除，核对 findAll 顺序与旧快照独立
 */
public final class GenericsExercises {
    private GenericsExercises() {}

    /** 练习1：把 source 逐个追加到 target（PECS）。 */
    static <T> void copy(List<? super T> target, List<? extends T> source) {
        for (T value : source) {
            target.add(value);
        }
    }

    /** 练习2：有界版本，T 必须是 Number 或其子类型。 */
    static <T extends Number> void copyNumbers(List<? super T> target, List<? extends T> source) {
        for (T value : source) {
            target.add(value);
        }
    }

    public static void main(String[] args) {
        // 练习1：Integer 来源 → Number 目标；复制 [1,2] 后目标 [1,2]
        List<Integer> ints = List.of(1, 2);
        List<Number> numbers = new ArrayList<>();
        copy(numbers, ints);
        System.out.println("练习1-target=" + numbers);
        // 通配符：? extends T 只安全读出 T；? super T 只安全写入 T

        // 练习2：上界 Number；整数来源 + Number 目标可编译
        List<Number> numbers2 = new ArrayList<>();
        copyNumbers(numbers2, List.of(3, 4));
        System.out.println("练习2-numbers=" + numbers2);
        // 字符串不是 Number 子类型：copyNumbers(numbers2, List.of("a")) → 编译失败

        // 练习3：插入两项 → 替换首项 → 删除次项；旧快照不变
        Repository<String, Integer> repo = new InMemoryRepository<>();
        repo.save(1, "A");
        System.out.println("练习3-step1=" + repo.findAll()); // [A]
        repo.save(2, "B");
        List<String> before = repo.findAll();
        System.out.println("练习3-step2=" + before); // [A, B]
        repo.save(1, "C");
        System.out.println("练习3-step3=" + repo.findAll()); // [C, B] 同键替换保原位置
        repo.deleteById(2);
        System.out.println("练习3-step4=" + repo.findAll()); // [C]
        System.out.println("练习3-before快照=" + before); // 仍为 [A, B]
    }
}
