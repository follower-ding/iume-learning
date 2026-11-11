package learn.lesson04_input;

import java.util.Scanner;

public class SumTwoProbe {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("第一个整数：");
            int first = scanner.nextInt();
            System.out.print("第二个整数：");
            int second = scanner.nextInt();
            System.out.println("和=" + (first + second));
        }
    }
}
