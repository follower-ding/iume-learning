package learn.advanced.files;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
public final class TextFiles {
    private TextFiles() { }
    public static void writeLines(Path path, List<String> lines) throws IOException {
        Path parent = path.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        // 明确用 UTF-8 保存中文；Files.write 会在写完或失败时关闭文件。
        Files.write(path, lines, StandardCharsets.UTF_8);
    }
    public static List<String> readLines(Path path) throws IOException {
        // 小文件可一次读入内存；异常直接交给调用者，避免伪装成空文件。
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }
}
