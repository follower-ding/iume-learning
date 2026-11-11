package learn.lesson02_variables;

public class VariablesDemo {
    public static void main(String[] args) {
        int age = 18;
        long population = 1_400_000_000L;
        double height = 1.75;
        char level = 'A';
        boolean learning = true;
        String name = "小明";

        // 变量名可以反复使用，右边先计算，结果再保存到左边。
        age = age + 1;
        int truncatedHeight = (int) height;

        System.out.println("姓名=" + name);
        System.out.println("年龄=" + age);
        System.out.println("人口=" + population);
        System.out.println("等级=" + level);
        System.out.println("正在学习=" + learning);
        System.out.println("强制转换后的身高=" + truncatedHeight);
    }
}
