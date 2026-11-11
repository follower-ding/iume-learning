package learn.lesson06_loops;

/** 练习1：输出 1 到 10 的偶数 */
public class EvenForProbe {
    public static void main(String[] args) {
        for (int number = 2; number <= 10; number += 2) {
            System.out.println(number);
        }
    }
}
