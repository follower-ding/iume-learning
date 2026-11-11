package learn.web.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public final class ProductHttpServer implements AutoCloseable {
    private static final int MAX_BODY_BYTES = 64 * 1024;
    private final HttpServer server;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final ObjectMapper mapper = new ObjectMapper();
    private final ConcurrentMap<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public ProductHttpServer(int port) throws IOException {
        if (port < 0 || port > 65535) throw new IllegalArgumentException("端口超出范围");
        // 只监听 IPv4 回环；端口 0 由系统选空闲端口。
        server = HttpServer.create(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), port), 0);
        server.createContext("/", this::handle);
        server.setExecutor(executor);
        server.start();
    }

    public URI uri(String path) { return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + path); }

    private void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String path = exchange.getRequestURI().getRawPath();
            String method = exchange.getRequestMethod();
            if (path.equals("/products")) {
                if (!method.equals("POST")) { methodNotAllowed(exchange, "POST"); return; }
                create(exchange);
                return;
            }
            if (path.matches("/products/[1-9][0-9]*")) {
                if (!method.equals("GET")) { methodNotAllowed(exchange, "GET"); return; }
                long id;
                try { id = Long.parseLong(path.substring("/products/".length())); }
                catch (NumberFormatException invalid) { reply(exchange, 404, Map.of("error", "商品不存在")); return; }
                Product product = products.get(id);
                reply(exchange, product == null ? 404 : 200,
                        product == null ? Map.of("error", "商品不存在") : product);
                return;
            }
            reply(exchange, 404, Map.of("error", "路径不存在"));
        }
    }

    private void create(HttpExchange exchange) throws IOException {
        // 上限按 UTF-8 字节计算，多读一字节才能区分恰好达到上限与超限。
        byte[] body = exchange.getRequestBody().readNBytes(MAX_BODY_BYTES + 1);
        if (body.length > MAX_BODY_BYTES) { reply(exchange, 413, Map.of("error", "请求正文过大")); return; }
        try {
            JsonNode json = mapper.readTree(body);
            if (json == null || !json.isObject()) throw new IllegalArgumentException();
            JsonNode name = json.get("name");
            JsonNode price = json.get("priceInCents");
            if (name == null || !name.isString() || name.stringValue().isBlank()
                    || price == null || !price.isIntegralNumber() || !price.canConvertToInt()
                    || price.intValue() < 0) throw new IllegalArgumentException();
            long id = nextId.getAndIncrement();
            Product product = new Product(id, name.stringValue(), price.intValue());
            products.put(id, product);
            exchange.getResponseHeaders().set("Location", "/products/" + id);
            reply(exchange, 201, product);
        } catch (JacksonException | IllegalArgumentException invalid) {
            reply(exchange, 400, Map.of("error", "无效的商品 JSON"));
        }
    }

    private void methodNotAllowed(HttpExchange exchange, String allowed) throws IOException {
        exchange.getResponseHeaders().set("Allow", allowed);
        reply(exchange, 405, Map.of("error", "方法不允许"));
    }

    private void reply(HttpExchange exchange, int status, Object value) throws IOException {
        // JSON 字符串转义由 Jackson 负责，不能手工拼接用户输入。
        byte[] bytes = mapper.writeValueAsBytes(value);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var output = exchange.getResponseBody()) { output.write(bytes); }
    }

    @Override public void close() {
        server.stop(0);
        executor.shutdownNow();
    }
}
record Product(long id, String name, int priceInCents) {}
