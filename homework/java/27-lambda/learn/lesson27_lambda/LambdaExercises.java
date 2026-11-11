package learn.lesson27_lambda;

/**
 * 第27课三级练习（自敲）：
 * 1) 求和改求积：2*3 → 6
 * 2) threshold 10→15：12>15 → false
 * 3) Formatter：匿名类与 Lambda 都返回 "[value]"
 */
public final class LambdaExercises {
    private LambdaExercises() {}

    @FunctionalInterface
    interface Calculator {
        int calculate(int left, int right);
    }

    @FunctionalInterface
    interface Formatter {
        String format(String value);
    }

    public static void main(String[] args) {
        // 练习1：求积
        Calculator oldWay = new Calculator() {
            @Override
            public int calculate(int left, int right) {
                return left * right;
            }
        };
        Calculator lambda = (left, right) -> left * right;
        System.out.println("练习1匿名类=" + oldWay.calculate(2, 3));
        System.out.println("练习1Lambda=" + lambda.calculate(2, 3));

        // 练习2：阈值改 15
        int threshold = 15;
        java.util.function.Predicate<Integer> expensive = amount -> amount > threshold;
        System.out.println("练习2谓词=" + expensive.test(12));

        // 练习3：Formatter
        Formatter anon = new Formatter() {
            @Override
            public String format(String value) {
                return "[" + value + "]";
            }
        };
        Formatter fmtLambda = value -> "[" + value + "]";
        System.out.println("练习3匿名类=" + anon.format("A"));
        System.out.println("练习3Lambda=" + fmtLambda.format("A"));
    }
}
