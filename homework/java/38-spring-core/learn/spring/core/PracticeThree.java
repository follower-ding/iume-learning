package learn.spring.core;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 练习3 独立：两个配置、两个容器。
 * 证明：注入的仓储决定行为；同一容器默认单例；关闭后 isActive() 为 false。
 * 两个容器之间不共享单例。
 */
public class PracticeThree {
    @Configuration
    static class HelloConfig {
        @Bean
        MessageRepository repository() {
            return name -> "你好，" + name;
        }

        @Bean
        GreetingService service(MessageRepository repository) {
            return new GreetingService(repository);
        }
    }

    @Configuration
    static class WelcomeConfig {
        @Bean
        MessageRepository repository() {
            return name -> "欢迎，" + name;
        }

        @Bean
        GreetingService service(MessageRepository repository) {
            return new GreetingService(repository);
        }
    }

    public static void main(String[] args) {
        var hello = new AnnotationConfigApplicationContext(HelloConfig.class);
        var welcome = new AnnotationConfigApplicationContext(WelcomeConfig.class);
        try {
            System.out.println(hello.getBean(GreetingService.class).greet("小明"));
            System.out.println(welcome.getBean(GreetingService.class).greet("小明"));
            System.out.println(hello.getBean(GreetingService.class) == hello.getBean(GreetingService.class));
        } finally {
            hello.close();
            welcome.close();
        }
        System.out.println(!hello.isActive() && !welcome.isActive());
    }
}
