package learn.lesson07_arrays;

public class AverageProbe {
    static double average(int[] values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("数组不能为空");
        }
        int sum = 0;
        for (int value : values) {
            sum += value;
        }
        return (double) sum / values.length;
    }

    public static void main(String[] args) {
        System.out.println("average(1,2)=" + average(new int[]{1, 2}));
        System.out.println("average(72,95,81)=" + average(new int[]{72, 95, 81}));
    }
}
