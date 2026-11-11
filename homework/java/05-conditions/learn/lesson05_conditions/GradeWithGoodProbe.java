package learn.lesson05_conditions;

/**
 * 练习2：给成绩加「良好」（80~89）。仅练习副本；主课仍是三级规则。
 */
public class GradeWithGoodProbe {
    public static String grade(int score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("成绩必须在 0 到 100 之间");
        }
        if (score >= 90) {
            return "优秀";
        } else if (score >= 80) {
            return "良好";
        } else if (score >= 60) {
            return "及格";
        } else {
            return "不及格";
        }
    }

    public static void main(String[] args) {
        System.out.println("90=" + grade(90));
        System.out.println("89=" + grade(89));
        System.out.println("80=" + grade(80));
        System.out.println("79=" + grade(79));
        System.out.println("59=" + grade(59));
    }
}
