package learn.lesson29_annotations;

import java.lang.reflect.Method;

public final class AnnotationBasicsDemo {
    private AnnotationBasicsDemo() {}
    public static void main(String[] args) throws NoSuchMethodException {
        AnnotationDemo demo = new AnnotationDemo();
        Method method = AnnotationDemo.class.getDeclaredMethod("annotatedExample");
        System.out.println("标签=" + method.getAnnotation(DemoCase.class).value());
        System.out.println("读取后次数=" + demo.invocationCount());
        demo.annotatedExample(); // 注解本身不会执行方法。
        System.out.println("调用后次数=" + demo.invocationCount());
    }
}
