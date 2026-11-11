package learn.spring.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** @Configuration 声明配置类；@Bean 的返回值交给容器管理。 */
@Configuration
public class AppConfig {
    @Bean
    public MessageRepository messageRepository() {
        return new InMemoryMessageRepository();
    }

    @Bean
    public GreetingService greetingService(MessageRepository repository) {
        // Spring 按类型把上面的 MessageRepository Bean 传进来。
        return new GreetingService(repository);
    }
}
