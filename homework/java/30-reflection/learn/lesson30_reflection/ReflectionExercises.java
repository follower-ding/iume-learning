package learn.lesson30_reflection;

import learn.lesson29_annotations.DemoCase;
import learn.lesson29_annotations.MiniComponent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 第30课三级练习（自敲，先预测再跑，对照参考答案）：
 * 1) register 重复则 IllegalStateException（构造计数仍 1）
 * 2) 非法签名报错拼 declaringClass.method；先全检再执行
 * 3) Field.getGenericType 保留声明泛型；运行时实例 class 擦除
 */
public final class ReflectionExercises {
    private ReflectionExercises() {}

    /** 练习1：重复注册直接拒绝，不复用实例。 */
    public static final class StrictContainer {
        private final Map<Class<?>, Object> components = new HashMap<>();

        public <T> T register(Class<T> type) {
            Objects.requireNonNull(type, "type");
            if (!type.isAnnotationPresent(MiniComponent.class)) {
                throw new IllegalArgumentException("not a mini component: " + type.getName());
            }
            // 与本课 MiniContainer 不同：已有 key 就抛，而不是返回旧实例。
            if (components.containsKey(type)) {
                throw new IllegalStateException("already registered: " + type.getName());
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
    }

    /** 练习2：非法签名消息带「类名.方法名」；仍先全检再执行。 */
    public static final class StrictDemoRunner {
        public List<String> run(Object target) {
            Objects.requireNonNull(target, "target");
            List<Method> methods = Arrays.stream(target.getClass().getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(DemoCase.class))
                    .sorted(Comparator.comparing(Method::getName))
                    .toList();

            // 先扫完所有标记方法；有非法签名就立刻报，一个都不 invoke。
            for (Method method : methods) {
                if (method.getParameterCount() != 0) {
                    String fullName = method.getDeclaringClass().getName() + "." + method.getName();
                    throw new IllegalArgumentException("demo method must have no parameters: " + fullName);
                }
            }

            List<String> labels = new ArrayList<>();
            for (Method method : methods) {
                try {
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

    /** 练习3：字段声明上的泛型信息 vs 实例上的擦除。 */
    static final class Holder {
        List<String> names;
    }

    @MiniComponent
    static final class CountedComponent {
        static int constructions;
        CountedComponent() { constructions++; }
    }

    static final class InvalidDemos {
        int invocations;

        @DemoCase("valid")
        private void alpha() { invocations++; }

        @DemoCase("invalid")
        private void zInvalid(String argument) { invocations++; }
    }

    public static void main(String[] args) throws Exception {
        // —— 练习1 ——
        // 预测：第一次 register 成功，构造=1；第二次抛 IllegalStateException，构造仍=1
        CountedComponent.constructions = 0;
        StrictContainer strict = new StrictContainer();
        strict.register(CountedComponent.class);
        boolean dupRejected = false;
        try {
            strict.register(CountedComponent.class);
        } catch (IllegalStateException ex) {
            dupRejected = true;
            System.out.println("练习1重复消息含 already registered=" + ex.getMessage().contains("already registered"));
        }
        System.out.println("练习1重复抛异常=" + dupRejected);
        System.out.println("练习1构造计数=" + CountedComponent.constructions);

        // —— 练习2 ——
        // 预测：消息含 DeclaringClass.zInvalid；alpha 不会被执行（invocations=0）
        InvalidDemos demos = new InvalidDemos();
        boolean badSig = false;
        String errMsg = "";
        try {
            new StrictDemoRunner().run(demos);
        } catch (IllegalArgumentException ex) {
            badSig = true;
            errMsg = ex.getMessage();
        }
        String expectedName = InvalidDemos.class.getName() + ".zInvalid";
        System.out.println("练习2非法签名报错=" + badSig);
        System.out.println("练习2消息含类名.方法=" + errMsg.contains(expectedName));
        System.out.println("练习2先全检未执行=" + (demos.invocations == 0));

        // —— 练习3 ——
        // 预测：getGenericType 含 List<String>；两个 ArrayList 实例的 class 相同（擦除）
        Type generic = Holder.class.getDeclaredField("names").getGenericType();
        Class<?> stringListClass = new ArrayList<String>().getClass();
        Class<?> integerListClass = new ArrayList<Integer>().getClass();
        System.out.println("练习3声明泛型=" + generic);
        System.out.println("练习3声明含String=" + generic.getTypeName().contains("String"));
        System.out.println("练习3实例class相同=" + (stringListClass == integerListClass));
        System.out.println("练习3擦除后class=" + stringListClass.getName());
    }
}
