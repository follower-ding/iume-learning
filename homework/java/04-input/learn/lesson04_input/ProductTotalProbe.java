package learn.lesson04_input;

import java.util.Scanner;

public class ProductTotalProbe {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("商品名：");
            String name = scanner.nextLine();
            System.out.print("整数单价：");
            if (!scanner.hasNextInt()) {
                System.out.println("单价必须是整数");
                return;
            }
            int price = scanner.nextInt();
            System.out.print("数量：");
            if (!scanner.hasNextInt()) {
                System.out.println("数量必须是整数");
                return;
            }
            int quantity = scanner.nextInt();
            System.out.println(name + "总价=" + price * quantity);
        }
    }
}
