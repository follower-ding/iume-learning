package learn.spring.mvc;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * 练习1 模仿：不占用 --serve，用端口 0 启动后请求 /greetings?name=阿青。
 * “阿青”须做 UTF-8 URL 编码：%E9%98%BF%E9%9D%92。
 */
public class PracticeOne {
    public static void main(String[] args) throws Exception {
        try (var server = MvcDemo.start(0);
             var client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()) {
            var request = HttpRequest.newBuilder(
                    URI.create("http://127.0.0.1:" + server.port() + "/greetings?name=%E9%98%BF%E9%9D%92"))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("GET /greetings?name=阿青: " + response.statusCode() + " " + response.body());
        }
    }
}
