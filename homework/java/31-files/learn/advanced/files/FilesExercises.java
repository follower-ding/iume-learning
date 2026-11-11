package learn.advanced.files;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

/**
 * 第31课三级练习（自敲，先预测再跑）：
 * 1) 两行改成「上海」「学习 Java」→ 读取结果：[上海, 学习 Java]
 * 2) 嵌套 notes/day1.txt；writeLines 建父目录；finally 依次删 nested、notes、directory
 * 3) 读不存在文件，catch NoSuchFileException 打印简单类名
 */
public final class FilesExercises {
    private FilesExercises() {}

    public static void main(String[] args) throws Exception {
        exercise1();
        exercise2();
        exercise3();
    }

    /** 练习1：模仿改两行内容 */
    static void exercise1() throws Exception {
        Path directory = Files.createTempDirectory("java-advanced-ex1-");
        Path file = directory.resolve("notes.txt");
        try {
            // 预测：读取结果：[上海, 学习 Java]
            TextFiles.writeLines(file, List.of("上海", "学习 Java"));
            System.out.println("练习1：" + "读取结果：" + TextFiles.readLines(file));
        } finally {
            Files.deleteIfExists(file);
            Files.deleteIfExists(directory);
        }
    }

    /** 练习2：嵌套路径；finally 须先删文件再删 notes 再删临时根目录 */
    static void exercise2() throws Exception {
        Path directory = Files.createTempDirectory("java-advanced-ex2-");
        Path nested = directory.resolve("notes/day1.txt");
        try {
            TextFiles.writeLines(nested, List.of("第一天", "文件 IO"));
            System.out.println("练习2：" + "读取结果：" + TextFiles.readLines(nested));
            System.out.println("练习2：父目录存在=" + Files.isDirectory(directory.resolve("notes")));
        } finally {
            // 目录非空时不能直接删父目录，须由内向外
            Files.deleteIfExists(nested);
            Files.deleteIfExists(directory.resolve("notes"));
            Files.deleteIfExists(directory);
        }
    }

    /** 练习3：读不存在文件，只捕 NoSuchFileException */
    static void exercise3() {
        try {
            TextFiles.readLines(Path.of("不存在的文件.txt"));
        } catch (NoSuchFileException e) {
            System.out.println("练习3：" + e.getClass().getSimpleName());
        } catch (Exception e) {
            // 其他异常不吞，继续抛出
            throw new RuntimeException(e);
        }
    }
}
