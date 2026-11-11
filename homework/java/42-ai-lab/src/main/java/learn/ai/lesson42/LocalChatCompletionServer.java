package learn.ai.lesson42;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本机的 OpenAI 兼容替身。它不调用大模型：看见资料就回固定句子，看见工具声明就先要求调用 lookupOrder。
 * 占位令牌 local-demo 只用于这个替身，不是服务商密钥。
 */
public final class LocalChatCompletionServer implements AutoCloseable {
    public static final String DEMO_TOKEN = "local-demo";
    private final HttpServer server;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile String lastPath = "";
    private volatile String lastAuthorization = "";
    private volatile String lastBody = "";

    public LocalChatCompletionServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 0);
        server.createContext("/v1/chat/completions", this::handle);
        server.setExecutor(executor);
        server.start();
    }

    public String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/v1";
    }

    public String lastPath() { return lastPath; }
    public String lastAuthorization() { return lastAuthorization; }
    public String lastBody() { return lastBody; }

    private void handle(HttpExchange exchange) throws IOException {
        byte[] raw = exchange.getRequestBody().readAllBytes();
        String path = exchange.getRequestURI().getPath();
        String authorization = exchange.getRequestHeaders().getFirst("Authorization");
        String body = new String(raw, StandardCharsets.UTF_8);
        lastPath = path;
        lastAuthorization = authorization == null ? "" : authorization;
        lastBody = body;
        if (!"POST".equals(exchange.getRequestMethod()) || !"/v1/chat/completions".equals(path)) {
            reply(exchange, 404, "{\"error\":\"not_found\"}");
            return;
        }
        if (!("Bearer " + DEMO_TOKEN).equals(authorization)) {
            reply(exchange, 401, "{\"error\":\"unauthorized\"}");
            return;
        }
        reply(exchange, 200, choose(body));
    }

    static String choose(String body) {
        // 工具结果里才有“已发货”。先看结果，再决定要不要发起调用。
        if (body.contains("已发货")) return assistantText("订单 1001 的状态是已发货。");
        if (body.contains("\"tools\"") && body.contains("lookupOrder")) return toolCall();
        if (body.contains("签收后7天内可申请退款")) return assistantText("根据资料，签收后7天内可以退款。");
        return assistantText("不知道。");
    }

    private static String assistantText(String content) {
        return "{"
                + "\"id\":\"chatcmpl-local\","
                + "\"created\":1,"
                + "\"model\":\"local-echo\","
                + "\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":" + quote(content) + "},"
                + "\"finish_reason\":\"stop\"}]}";
    }

    private static String toolCall() {
        String arguments = quote("{\"orderId\":\"1001\"}");
        return "{"
                + "\"id\":\"chatcmpl-local\","
                + "\"created\":1,"
                + "\"model\":\"local-echo\","
                + "\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"tool_calls\":[{"
                + "\"id\":\"call-1001\",\"type\":\"function\",\"function\":{\"name\":\"lookupOrder\",\"arguments\":" + arguments + "}}]},"
                + "\"finish_reason\":\"tool_calls\"}]}";
    }

    private static String quote(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static void reply(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var output = exchange.getResponseBody()) { output.write(bytes); }
    }

    @Override
    public void close() {
        server.stop(0);
        executor.shutdownNow();
    }
}
