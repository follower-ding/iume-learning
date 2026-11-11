package learn.lesson05_conditions;

public class ConditionsDemo {
    public static String grade(int score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("成绩必须在 0 到 100 之间");
        }
        if (score >= 90) {
            return "优秀";
        } else if (score >= 60) {
            return "及格";
        } else {
            return "不及格";
        }
    }

    public static void main(String[] args) {
        int score = 85;
        System.out.println("成绩=" + score);
        System.out.println("等级=" + grade(score));
    }
}
