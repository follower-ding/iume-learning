package learn.web.http;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * 第35课三级练习（自敲，先预测再跑；仅本机回环，不访问公网）：
 * 1) POST 钢笔/1200 → 201 + JSON
 * 2) POST 缺 priceInCents → 400，随后 GET /products/1 → 404
 * 3) PUT /products → 405(+Allow)、GET /products/99 → 404、超大 POST → 413
 */
public final class HttpJsonExercises {
    private HttpJsonExercises() {}

    public static void main(String[] args) throws Exception {
        exercise1();
        exercise2();
        exercise3();
    }

    /** 练习1：模仿 — 钢笔 / 1200 分，预测 201 + {"id":1,"name":"钢笔","priceInCents":1200} */
    static void exercise1() throws Exception {
        try (ProductHttpServer server = new ProductHttpServer(0);
             HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build()) {
            HttpRequest create = HttpRequest.newBuilder(server.uri("/products"))
                    .timeout(Duration.ofSeconds(3))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"钢笔\",\"priceInCents\":1200}"))
                    .build();
            HttpResponse<String> created = client.send(create, HttpResponse.BodyHandlers.ofString());
            // 预测：状态 201，正文 {"id":1,"name":"钢笔","priceInCents":1200}
            System.out.println("练习1：状态=" + created.statusCode() + " 正文=" + created.body());
        }
    }

    /** 练习2：修改 — 缺 priceInCents → create 400，且不发号；再 GET /products/1 → 404 */
    static void exercise2() throws Exception {
        try (ProductHttpServer server = new ProductHttpServer(0);
             HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build()) {
            HttpRequest create = HttpRequest.newBuilder(server.uri("/products"))
                    .timeout(Duration.ofSeconds(3))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"钢笔\"}"))
                    .build();
            HttpResponse<String> bad = client.send(create, HttpResponse.BodyHandlers.ofString());
            HttpRequest get = HttpRequest.newBuilder(server.uri("/products/1"))
                    .timeout(Duration.ofSeconds(3)).GET().build();
            HttpResponse<String> missing = client.send(get, HttpResponse.BodyHandlers.ofString());
            // 预测：create 400；/products/1 → 404
            System.out.println("练习2：create状态=" + bad.statusCode()
                    + " /products/1状态=" + missing.statusCode());
        }
    }

    /** 练习3：独立 — PUT→405(+Allow)、GET/99→404、超大POST→413 */
    static void exercise3() throws Exception {
        try (ProductHttpServer server = new ProductHttpServer(0);
             HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build()) {
            HttpRequest put = HttpRequest.newBuilder(server.uri("/products"))
                    .timeout(Duration.ofSeconds(3))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> putResp = client.send(put, HttpResponse.BodyHandlers.ofString());
            String allow = putResp.headers().firstValue("Allow").orElse("(无)");

            HttpRequest get99 = HttpRequest.newBuilder(server.uri("/products/99"))
                    .timeout(Duration.ofSeconds(3)).GET().build();
            HttpResponse<String> missing = client.send(get99, HttpResponse.BodyHandlers.ofString());

            // 65537 字节 > 64 KiB → 413
            String oversized = "x".repeat(65537);
            HttpRequest big = HttpRequest.newBuilder(server.uri("/products"))
                    .timeout(Duration.ofSeconds(5))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(oversized))
                    .build();
            HttpResponse<String> bigResp = client.send(big, HttpResponse.BodyHandlers.ofString());

            // 预测：PUT 405 Allow=POST；GET/99 404；超大 POST 413
            System.out.println("练习3：PUT状态=" + putResp.statusCode()
                    + " Allow=" + allow
                    + " /products/99状态=" + missing.statusCode()
                    + " 超大POST状态=" + bigResp.statusCode());
        }
    }
}
