package learn.lesson07_arrays;

public class ArraysDemo {
    public static int max(int[] values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("数组不能为空");
        }
        int largest = values[0];
        for (int index = 1; index < values.length; index++) {
            if (values[index] > largest) {
                largest = values[index];
            }
        }
        return largest;
    }

    public static void main(String[] args) {
        int[] scores = {72, 95, 81};
        System.out.println("数组长度=" + scores.length);
        for (int index = 0; index < scores.length; index++) {
            System.out.println("scores[" + index + "]=" + scores[index]);
        }
        System.out.println("最高分=" + max(scores));
    }
}
