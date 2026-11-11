package learn.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Boot 自动配置 Web、DataSource 和事务；MapperScan 注册 XML Mapper 的代理。 */
@SpringBootApplication
@MapperScan("learn.boot")
public class LearningApplication {
    public static void main(String[] args) {
        if (args.length > 0 && "--demo".equals(args[0])) {
            try (var context = SpringApplication.run(LearningApplication.class, "--server.port=0")) {
                var product = context.getBean(ProductService.class).create("演示纸笔", 12);
                System.out.println("Boot 创建: " + product.getId() + " " + product.getName() + " " + product.getPrice());
            }
        } else {
            SpringApplication.run(LearningApplication.class, args);
        }
    }
}
