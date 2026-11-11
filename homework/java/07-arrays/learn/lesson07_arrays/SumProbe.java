package learn.lesson07_arrays;

public class SumProbe {
    public static void main(String[] args) {
        int[] scores = {72, 95, 81};
        int sum = 0;
        for (int score : scores) {
            sum += score;
        }
        System.out.println("总分=" + sum);
    }
}
