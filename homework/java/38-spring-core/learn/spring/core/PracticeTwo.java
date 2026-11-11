package learn.spring.core;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 练习2 修改：独立 FormalConfig，只注册 FormalMessageRepository，
 * 不改课程 AppConfig，避免两个仓储抢同一个类型。
 */
public class PracticeTwo {
    static class FormalMessageRepository implements MessageRepository {
        @Override
        public String messageFor(String name) {
            return "您好，" + name;
        }
    }

    @Configuration
    static class FormalConfig {
        @Bean
        MessageRepository repository() {
            return new FormalMessageRepository();
        }

        @Bean
        GreetingService service(MessageRepository repository) {
            return new GreetingService(repository);
        }
    }

    public static void main(String[] args) {
        try (var context = new AnnotationConfigApplicationContext(FormalConfig.class)) {
            System.out.println(context.getBean(GreetingService.class).greet("小明"));
        }
    }
}
