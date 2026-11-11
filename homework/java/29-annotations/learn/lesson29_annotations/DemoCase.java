package learn.lesson29_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/** 只用于方法且在运行时可见的演示标签，供 DemoRunner 读取。 */
public @interface DemoCase {
    String value();
}
