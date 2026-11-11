package learn.lesson30_reflection;

import learn.lesson29_annotations.AnnotationDemo;
import learn.lesson29_annotations.MiniComponent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

/** 展示读取构造器、字段、方法，以及受控运行演示和组件注册。 */
public final class ReflectionDemo {
    private ReflectionDemo() {}

    public static void main(String[] args) {
        Class<AnnotationDemo> type = AnnotationDemo.class;
        System.out.println("class: " + type.getName());
        Arrays.stream(type.getDeclaredConstructors())
                .map(Constructor::getName).sorted()
                .forEach(name -> System.out.println("constructor: " + name));
        Arrays.stream(type.getDeclaredFields())
                .map(Field::getName).sorted()
                .forEach(name -> System.out.println("field: " + name));
        Arrays.stream(type.getDeclaredMethods())
                .sorted(Comparator.comparing(Method::getName))
                .forEach(method -> System.out.println("method: " + method.getName()));

        AnnotationDemo demo = new AnnotationDemo();
        System.out.println("labels: " + new DemoRunner().run(demo));
        System.out.println("invocations: " + demo.invocationCount());

        MiniContainer container = new MiniContainer();
        GreetingComponent first = container.register(GreetingComponent.class);
        System.out.println("same component: " + (first == container.get(GreetingComponent.class)));
        System.out.println("same on duplicate registration: "
                + (first == container.register(GreetingComponent.class)));
    }

    @MiniComponent
    private static class GreetingComponent {}
}
