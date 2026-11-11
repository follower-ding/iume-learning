package learn.advanced.files;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
public class FilesDemo {
    public static void main(String[] args) throws Exception {
        // 每次运行都创建独立目录，避免覆盖学习者自己的文件。
        Path directory = Files.createTempDirectory("java-advanced-");
        Path file = directory.resolve("notes.txt");
        try {
            TextFiles.writeLines(file, List.of("你好", "Java"));
            System.out.println("读取结果：" + TextFiles.readLines(file));
        } finally {
            // 只删除本例创建的文件，不递归删除别人的文件。
            Files.deleteIfExists(file);
            Files.deleteIfExists(directory);
        }
    }
}
