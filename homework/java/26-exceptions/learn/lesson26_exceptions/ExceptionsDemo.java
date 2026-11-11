package learn.lesson26_exceptions;

import java.io.IOException;
import java.io.StringReader;

public final class ExceptionsDemo {
    private ExceptionsDemo() {}

    private static String readOne() throws IOException { // 受检异常向调用方声明。
        try (StringReader reader = new StringReader("A")) { // 自动关闭资源。
            return String.valueOf((char) reader.read());
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("读取=" + readOne());
            throw new IllegalArgumentException("数量无效"); // 主动抛出非受检异常。
        } catch (IOException exception) {
            throw new IllegalStateException("读取失败", exception); // 保留根因。
        } catch (IllegalArgumentException exception) {
            System.out.println("捕获=" + exception.getMessage());
        } finally {
            System.out.println("收尾=执行"); // 不在 finally 返回，避免覆盖结果或异常。
        }
    }
}
