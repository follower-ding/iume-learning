package learn.lesson06_loops;

public class LoopsDemo {
    public static int sumTo(int end) {
        if (end < 0 || end > 10_000) {
            throw new IllegalArgumentException("end 必须在 0 到 10000 之间");
        }
        int sum = 0;
        for (int number = 1; number <= end; number++) {
            sum += number;
        }
        return sum;
    }

    public static void main(String[] args) {
        for (int number = 1; number <= 3; number++) {
            System.out.println("for 第 " + number + " 次");
        }

        int countdown = 3;
        while (countdown > 0) {
            System.out.println("倒计时=" + countdown);
            countdown--;
        }
        System.out.println("1 到 5 的和=" + sumTo(5));
    }
}
