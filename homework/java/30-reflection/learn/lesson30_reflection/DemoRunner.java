package learn.lesson30_reflection;

import learn.lesson29_annotations.DemoCase;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** 按名称排序并执行标记了 DemoCase 的无参方法，保留反射失败原因。 */
public final class DemoRunner {
    public List<String> run(Object target) {
        Objects.requireNonNull(target, "target");
        // getDeclaredMethods 的原始次序不保证稳定，显式按方法名排序。
        List<Method> methods = Arrays.stream(target.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(DemoCase.class))
                .sorted(Comparator.comparing(Method::getName))
                .toList();

        for (Method method : methods) {
            if (method.getParameterCount() != 0) {
                throw new IllegalArgumentException("demo method must have no parameters: " + method.getName());
            }
        }

        List<String> labels = new ArrayList<>();
        for (Method method : methods) {
            try {
                // 私有成员访问可能受模块边界限制，不能假定总能打开。
                if (!method.trySetAccessible()) {
                    throw new IllegalAccessException("method is not accessible");
                }
                method.invoke(target);
                labels.add(method.getAnnotation(DemoCase.class).value());
            } catch (ReflectiveOperationException | SecurityException exception) {
                throw new IllegalStateException("failed to run demo: " + method.getName(), exception);
            }
        }
        return labels;
    }
}
