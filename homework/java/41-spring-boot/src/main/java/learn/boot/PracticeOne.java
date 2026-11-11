package learn.boot;

import java.net.http.HttpClient;
import java.time.Duration;

/** 模仿：创建名称「橡皮」、价格 5 的商品，打印状态、Location 和 JSON。 */
public class PracticeOne {
    public static void main(String[] args) throws Exception {
        try (var context = PracticeSupport.start("practice_one");
             var client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()) {
            var response = PracticeSupport.request(client, PracticeSupport.port(context),
                "POST", "/products", "{\"name\":\"橡皮\",\"price\":5}");
            System.out.println("状态: " + response.statusCode());
            System.out.println("Location: " + response.headers().firstValue("Location").orElse("(无)"));
            System.out.println("JSON: " + response.body());
        }
    }
}
