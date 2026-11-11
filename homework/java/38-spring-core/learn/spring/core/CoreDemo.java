package learn.spring.core;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/** 先手工装配，再让 Spring 容器装配，比较输出和单例身份。 */
public class CoreDemo {
    public static void main(String[] args) {
        var manual = new GreetingService(new InMemoryMessageRepository());
        System.out.println("手工构造: " + manual.greet("小明"));

        try (var context = new AnnotationConfigApplicationContext(AppConfig.class)) {
            var service = context.getBean(GreetingService.class);
            System.out.println("Spring 容器: " + service.greet("小明"));
            System.out.println("默认单例: " + (service == context.getBean(GreetingService.class)));
        }
    }
}
