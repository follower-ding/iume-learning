package learn.spring.core;

/** 练习1 模仿：手工构造器注入，打印 欢迎，小明。不启动 Spring 容器。 */
public class PracticeOne {
    public static void main(String[] args) {
        MessageRepository alternate = name -> "欢迎，" + name;
        System.out.println(new GreetingService(alternate).greet("小明"));
    }
}
