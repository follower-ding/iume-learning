package learn.advanced.network;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
public class NetworkDemo {
    public static HttpResponse<String> fetch(URI uri) throws IOException, InterruptedException {
        // 连接与整次请求各有超时；关闭客户端会回收其连接资源。
        try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build()) {
            HttpRequest request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(3)).GET().build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }
    public static void main(String[] args) throws Exception {
        // 本地服务由 try-with-resources 保证退出时关闭。
        try (LocalGreetingServer server = new LocalGreetingServer()) {
            HttpResponse<String> response = fetch(server.uri("/hello"));
            System.out.println("状态：" + response.statusCode());
            System.out.println("正文：" + response.body());
            System.out.println("不存在的路径：" + fetch(server.uri("/missing")).statusCode());
        }
    }
}
