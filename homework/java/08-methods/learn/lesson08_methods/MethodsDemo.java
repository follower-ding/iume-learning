package learn.lesson08_methods;

public class MethodsDemo {
    public static int area(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("边长不能是负数");
        }
        return width * height;
    }

    public static double area(double width, double height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("边长不能是负数");
        }
        return width * height;
    }

    // 练习2：long 重载；area(3,4) 仍选 int；area(3L,4L) 选 long
    public static long area(long width, long height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("边长不能是负数");
        }
        return width * height;
    }

    private static String greeting(String name) {
        String sentence = "你好，" + name;
        return sentence;
    }

    // 练习1：平方
    public static int square(int number) {
        return number * number;
    }

    // 练习3：把值夹在 [min,max]
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
        int result = area(3, 4);
        System.out.println("整数矩形面积=" + result);
        System.out.println("小数矩形面积=" + area(2.5, 4.0));
        System.out.println(greeting("小明"));

        // 练习自检（不影响官方三行正文；单独跑练习副本时可见）
        System.out.println("square(5)=" + square(5));
        long longArea = area(3L, 4L);
        System.out.println("long面积=" + longArea);
        System.out.println("clamp(2,3,8)=" + clamp(2, 3, 8));
        System.out.println("clamp(5,3,8)=" + clamp(5, 3, 8));
        System.out.println("clamp(9,3,8)=" + clamp(9, 3, 8));
    }
}
