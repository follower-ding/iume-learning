package learn.lesson30_reflection;

import learn.lesson29_annotations.MiniComponent;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/** 只注册带 MiniComponent 标记的类；重复注册复用已有实例。 */
public final class MiniContainer {
    private final Map<Class<?>, Object> components = new HashMap<>();

    public <T> T register(Class<T> type) {
        Objects.requireNonNull(type, "type");
        if (!type.isAnnotationPresent(MiniComponent.class)) {
            throw new IllegalArgumentException("not a mini component: " + type.getName());
        }
        // 重复注册返回同一个组件，不重新运行构造器。
        Object existing = components.get(type);
        if (existing != null) {
            return type.cast(existing);
        }
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            if (!constructor.trySetAccessible()) {
                throw new IllegalAccessException("constructor is not accessible");
            }
            T instance = constructor.newInstance();
            components.put(type, instance);
            return instance;
        } catch (ReflectiveOperationException | SecurityException exception) {
            throw new IllegalStateException("failed to register component: " + type.getName(), exception);
        }
    }

    public <T> T get(Class<T> type) {
        Objects.requireNonNull(type, "type");
        Object instance = components.get(type);
        if (instance == null) {
            throw new IllegalStateException("component not registered: " + type.getName());
        }
        return type.cast(instance);
    }
}
