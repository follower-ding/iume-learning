package learn.lesson08_methods;

public class ClampProbe {
    public static int clamp(int value, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min 不能大于 max");
        }
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static void main(String[] args) {
        System.out.println("clamp(2,3,8)=" + clamp(2, 3, 8));
        System.out.println("clamp(5,3,8)=" + clamp(5, 3, 8));
        System.out.println("clamp(9,3,8)=" + clamp(9, 3, 8));
    }
}
