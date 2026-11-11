package learn.lesson18_common_apis;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class CommonApisDemo {
    public static String originalText() {
        String text = "abc";
        text.replace("b", "x");
        return text;
    }

    public static String replacedText() {
        return "abc".replace("b", "x");
    }

    public static int partsCount(String text) {
        return text.split(",").length;
    }

    public static LocalDate nextDay(String text) {
        return LocalDate.parse(text).plusDays(1);
    }

    public static BigDecimal sum() {
        // 用十进制字符串构造，避免先经过二进制浮点近似值。
        return new BigDecimal("0.1").add(new BigDecimal("0.2"));
    }

    public static BigDecimal round(String text) {
        return new BigDecimal(text).setScale(2, RoundingMode.HALF_UP);
    }

    public static int requireNumber(Integer number) {
        if (number == null) {
            throw new IllegalArgumentException("数字不能为空");
        }
        return number;
    }

    public static void main(String[] args) {
        System.out.println("原字符串=" + originalText());
        System.out.println("新字符串=" + replacedText());
        System.out.println("内容相同=" + "abc".equals(new String("abc")));
        System.out.println("分段数=" + partsCount("甲,乙,丙"));
        Integer wrapped = 7;
        int plain = requireNumber(wrapped);
        System.out.println("拆箱=" + plain);
        System.out.println("次日=" + nextDay("2026-09-30"));
        System.out.println("金额和=" + sum());
        System.out.println("四舍五入=" + round("1.005"));

        // 三道练习
        System.out.println("练习1分段数=" + partsCount("红,蓝"));
        System.out.println("练习2次日=" + nextDay("2026-12-31"));
        BigDecimal practice3 = new BigDecimal("0.7").add(new BigDecimal("0.8")).setScale(2, RoundingMode.HALF_UP);
        System.out.println("练习3金额=" + practice3);
    }
}
