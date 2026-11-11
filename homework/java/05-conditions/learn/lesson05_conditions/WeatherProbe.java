package learn.lesson05_conditions;

/** 练习1：按温度输出 冷 / 舒适 / 热（练习副本，不进讲义仓） */
public class WeatherProbe {
    public static String weather(int temperature) {
        if (temperature < 10) {
            return "冷";
        } else if (temperature < 25) {
            return "舒适";
        } else {
            return "热";
        }
    }

    public static void main(String[] args) {
        System.out.println("9=" + weather(9));
        System.out.println("10=" + weather(10));
        System.out.println("25=" + weather(25));
    }
}
