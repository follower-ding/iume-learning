package learn.web.http;

import java.net.http.*;
import java.time.Duration;

public final class HttpJsonDemo {
    private HttpJsonDemo() {}
    public static void main(String[] args) throws Exception {
        if (args.length == 2 && args[0].equals("--serve")) {
            int port = Integer.parseInt(args[1]);
            try (ProductHttpServer server = new ProductHttpServer(port)) {
                System.out.println("服务地址：" + server.uri("/products"));
                System.in.read();
            }
            return;
        }
        try (ProductHttpServer server = new ProductHttpServer(0);
             HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build()) {
            HttpRequest create = HttpRequest.newBuilder(server.uri("/products"))
                    .timeout(Duration.ofSeconds(3))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"中文\\\"书\",\"priceInCents\":2500}"))
                    .build();
            HttpResponse<String> created = client.send(create, HttpResponse.BodyHandlers.ofString());
            System.out.println("创建状态：" + created.statusCode());
            System.out.println("创建正文：" + created.body());
            HttpRequest get = HttpRequest.newBuilder(server.uri("/products/1"))
                    .timeout(Duration.ofSeconds(3)).GET().build();
            HttpResponse<String> found = client.send(get, HttpResponse.BodyHandlers.ofString());
            System.out.println("查询状态：" + found.statusCode());
            System.out.println("查询正文：" + found.body());
            HttpRequest missing = HttpRequest.newBuilder(server.uri("/products/99"))
                    .timeout(Duration.ofSeconds(3)).GET().build();
            System.out.println("不存在状态：" + client.send(missing, HttpResponse.BodyHandlers.ofString()).statusCode());
        }
    }
}
