package learn.boot;

import java.net.http.HttpClient;
import java.time.Duration;

/** 修改：价格 0、名称 101 字、未知 id，打印状态并说明在哪一层被拒绝。 */
public class PracticeTwo {
    public static void main(String[] args) throws Exception {
        try (var context = PracticeSupport.start("practice_two");
             var client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()) {
            int port = PracticeSupport.port(context);
            var zeroPrice = PracticeSupport.request(client, port, "POST", "/products",
                "{\"name\":\"零元笔\",\"price\":0}");
            var longName = PracticeSupport.request(client, port, "POST", "/products",
                "{\"name\":\"" + "甲".repeat(101) + "\",\"price\":1}");
            var missing = PracticeSupport.request(client, port, "GET", "/products/999999", null);
            System.out.println("价格0: " + zeroPrice.statusCode() + " 入口 @Valid/@Positive 拒绝");
            System.out.println("名称101字: " + longName.statusCode() + " 入口 @Valid/@Size(max=100) 拒绝");
            System.out.println("未知id: " + missing.statusCode() + " Service 抛 ProductNotFoundException，Advice 转 404");
        }
    }
}
