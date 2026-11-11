package learn.lesson06_loops;

/** 练习2：用 while 重写 sumTo */
public class SumToWhileProbe {
    public static int sumTo(int end) {
        if (end < 0 || end > 10_000) {
            throw new IllegalArgumentException("end 必须在 0 到 10000 之间");
        }
        int sum = 0;
        int number = 1;
        while (number <= end) {
            sum += number;
            number++;
        }
        return sum;
    }

    public static void main(String[] args) {
        System.out.println("sumTo(0)=" + sumTo(0));
        System.out.println("sumTo(5)=" + sumTo(5));
    }
}
