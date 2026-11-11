package learn.lesson29_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/** 只用于类型且在运行时可见的组件标记，供 MiniContainer 检查。 */
public @interface MiniComponent {}
