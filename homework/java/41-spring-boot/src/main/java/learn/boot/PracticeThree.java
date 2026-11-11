package learn.boot;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * 独立：先创建「纸笔」，再批量插入「事务甲」+「纸笔」。
 * 只看 409 不够，必须再用 Mapper 查真实 H2，确认「事务甲」被回滚。
 */
public class PracticeThree {
    public static void main(String[] args) throws Exception {
        try (var context = PracticeSupport.start("practice_three");
             var client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()) {
            int port = PracticeSupport.port(context);
            var created = PracticeSupport.request(client, port, "POST", "/products",
                "{\"name\":\"纸笔\",\"price\":12}");
            var batch = PracticeSupport.request(client, port, "POST", "/products/batch",
                "{\"firstName\":\"事务甲\",\"secondName\":\"纸笔\",\"price\":10}");
            var mapper = context.getBean(ProductMapper.class);
            System.out.println("创建纸笔: " + created.statusCode());
            System.out.println("批量: " + batch.statusCode());
            System.out.println("事务甲存在: " + (mapper.findByName("事务甲") != null));
            System.out.println("纸笔存在: " + (mapper.findByName("纸笔") != null));
            System.out.println("自调用风险: 类内 this.createTwo(...) 不经过 Spring 代理，@Transactional 不会生效，第一笔可能已提交");
        }
    }
}
