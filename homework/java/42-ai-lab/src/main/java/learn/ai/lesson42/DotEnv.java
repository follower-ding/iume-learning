package learn.ai.lesson42;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** 只解析 KEY=VALUE。不执行文件内容，也不打印值。 */
public final class DotEnv {
    private DotEnv() {}

    public static Path defaultPath() {
        Path moduleFile = Path.of("07-ai-lab", ".env");
        if (Files.isRegularFile(moduleFile)) return moduleFile;
        Path local = Path.of(".env");
        if (Files.isRegularFile(local)) return local;
        return moduleFile;
    }

    public static Map<String, String> load(Path path) throws IOException {
        if (path == null || !Files.isRegularFile(path)) return Map.of();
        return parse(Files.readString(path));
    }

    public static Map<String, String> parse(String text) {
        Map<String, String> values = new LinkedHashMap<>();
        if (text == null || text.isEmpty()) return Map.of();
        for (String raw : text.split("\\R", -1)) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            if (line.startsWith("export ")) line = line.substring("export ".length()).trim();
            int separator = line.indexOf('=');
            if (separator <= 0) continue;
            String key = line.substring(0, separator).trim();
            String value = unquote(line.substring(separator + 1).trim());
            if (!key.isEmpty()) values.put(key, value);
        }
        return Map.copyOf(values);
    }

    private static String unquote(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
