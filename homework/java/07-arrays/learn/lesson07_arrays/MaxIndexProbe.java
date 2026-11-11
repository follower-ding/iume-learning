package learn.lesson07_arrays;

public class MaxIndexProbe {
    public static void main(String[] args) {
        int[] scores = {72, 95, 81};
        int maxIndex = 0;
        for (int index = 1; index < scores.length; index++) {
            if (scores[index] > scores[maxIndex]) {
                maxIndex = index;
            }
        }
        System.out.println("最高分下标=" + maxIndex);
        System.out.println("最高分=" + scores[maxIndex]);
    }
}
