package learn.advanced.network;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 第34课三级练习（自敲，先预测再跑；仅本机回环，不访问公网）：
 * 1) /hello 正文改为「本地服务」，状态仍 200
 * 2) 新增 /ping → 200 / pong
 * 3) status(URI)：/hello→200、/missing→404
 */
public final class NetworkExercises {
    private NetworkExercises() {}

    public static void main(String[] args) throws Exception {
        exercise1();
        exercise2();
        exercise3();
    }

    /** 练习1：模仿 — 正文改为「本地服务」，预测状态 200、正文「本地服务」 */
    static void exercise1() throws Exception {
        try (ExerciseServer server = new ExerciseServer("本地服务", false)) {
            HttpResponse<String> response = NetworkDemo.fetch(server.uri("/hello"));
            // 预测：状态 200，正文 本地服务
            System.out.println("练习1：状态=" + response.statusCode() + " 正文=" + response.body());
        }
    }

    /** 练习2：修改 — 加 /ping，预测 200 / pong */
    static void exercise2() throws Exception {
        try (ExerciseServer server = new ExerciseServer("你好，HTTP！", true)) {
            HttpResponse<String> response = NetworkDemo.fetch(server.uri("/ping"));
            // 预测：状态 200，正文 pong
            System.out.println("练习2：/ping 状态=" + response.statusCode() + " 正文=" + response.body());
        }
    }

    /** 练习3：独立 — status(URI) 对 /hello→200、/missing→404 */
    static void exercise3() throws Exception {
        try (LocalGreetingServer server = new LocalGreetingServer()) {
            // 预测：/hello → 200，/missing → 404
            System.out.println("练习3：/hello=" + status(server.uri("/hello"))
                    + " /missing=" + status(server.uri("/missing")));
        }
    }

    static int status(URI uri) throws Exception {
        return NetworkDemo.fetch(uri).statusCode();
    }

    /** 练习用本机服务器：可改 /hello 正文，可选注册 /ping */
    static final class ExerciseServer implements AutoCloseable {
        private final HttpServer server;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();

        ExerciseServer(String helloBody, boolean withPing) throws IOException {
            server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
            server.createContext("/hello", exchange -> {
                byte[] body = helloBody.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                exchange.sendResponseHeaders(200, body.length);
                try (var output = exchange.getResponseBody()) {
                    output.write(body);
                }
            });
            if (withPing) {
                server.createContext("/ping", exchange -> {
                    byte[] body = "pong".getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                    exchange.sendResponseHeaders(200, body.length);
                    try (var out = exchange.getResponseBody()) {
                        out.write(body);
                    }
                });
            }
            server.setExecutor(executor);
            server.start();
        }

        URI uri(String path) {
            return URI.create("http://localhost:" + server.getAddress().getPort() + path);
        }

        @Override
        public void close() {
            server.stop(0);
            executor.shutdownNow();
        }
    }
}
