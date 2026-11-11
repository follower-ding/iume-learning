package learn.lesson04_input;

import java.util.Scanner;

public class AdultCheckProbe {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("请输入姓名：");
            String name = scanner.nextLine();
            System.out.print("请输入年龄：");

            if (!scanner.hasNextInt()) {
                System.out.println("年龄必须是整数，程序结束。");
                return;
            }

            int age = scanner.nextInt();
            if (age < 0 || age > 150) {
                System.out.println("年龄必须在 0 到 150 之间。");
                return;
            }
            if (age >= 18) {
                System.out.println("已成年");
            } else {
                System.out.println("未成年");
            }
            System.out.println("你好，" + name + "，明年你 " + (age + 1) + " 岁。");
        }
    }
}
