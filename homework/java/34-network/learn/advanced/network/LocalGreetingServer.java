package learn.advanced.network;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public final class LocalGreetingServer implements AutoCloseable {
    private final HttpServer server;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public LocalGreetingServer() throws IOException {
        // 回环地址只在本机访问；端口 0 由系统选空闲端口。
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        server.createContext("/hello", exchange -> {
            byte[] body = "你好，HTTP！".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            // try-with-resources 即使写入失败也会关闭响应正文流。
            try (var output = exchange.getResponseBody()) { output.write(body); }
        });
        server.setExecutor(executor);
        server.start();
    }
    public URI uri(String path) { return URI.create("http://localhost:" + server.getAddress().getPort() + path); }
    // 服务与线程池都要关，避免非守护线程阻止程序退出。
    @Override public void close() { server.stop(0); executor.shutdownNow(); }
}
