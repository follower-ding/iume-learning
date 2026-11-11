package learn.lesson29_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 第29课三级练习（自敲，先预测再跑，对照参考答案）：
 * 1) DemoCase 加 description() default "" —— 默认空串，显式得说明；value() 仍必填
 * 2) 自定义 @Marker（TYPE + RUNTIME），标类可读；标方法应编译失败（见注释）
 * 3) @RequiredField + 反射：仅注解不校验，读标记后再显式拒绝 null/空白
 */
public final class AnnotationExercises {
    private AnnotationExercises() {}

    /** 练习1：在 DemoCase 思路上加可选 description。 */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface DemoCaseEx {
        String value();
        String description() default "";
    }

    /** 练习2：只允许标在类型上，运行时可见。 */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Marker {}

    /** 练习3：字段「必填」元数据——自己不会拦空值。 */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface RequiredField {}

    @Marker
    static final class Example {}

    static final class OrderInput {
        @RequiredField String customerId;

        OrderInput(String customerId) {
            this.customerId = customerId;
        }
    }

    static final class DemoHolder {
        @DemoCaseEx("x")
        void noDescription() {}

        @DemoCaseEx(value = "x", description = "说明")
        void withDescription() {}
    }

    /** 读 RequiredField：有标记才检查；null 或空白就抛。 */
    static void requireNonBlank(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        if (!field.isAnnotationPresent(RequiredField.class)) {
            return; // 没标必填，本练习不拦
        }
        Object raw = field.get(target);
        if (raw == null || String.valueOf(raw).isBlank()) {
            throw new IllegalArgumentException(fieldName + " 不能为空");
        }
    }

    public static void main(String[] args) throws Exception {
        // —— 练习1 ——
        // 预测：无 description → ""；有 description → "说明"；value 仍必填
        Method m1 = DemoHolder.class.getDeclaredMethod("noDescription");
        Method m2 = DemoHolder.class.getDeclaredMethod("withDescription");
        DemoCaseEx a1 = m1.getAnnotation(DemoCaseEx.class);
        DemoCaseEx a2 = m2.getAnnotation(DemoCaseEx.class);
        System.out.println("练习1默认description=[" + a1.description() + "]");
        System.out.println("练习1显式description=[" + a2.description() + "]");
        System.out.println("练习1value仍必填=true（没写 value 编不过）");

        // —— 练习2 ——
        // 预测：标在类上 getAnnotation 非 null
        Marker marker = Example.class.getAnnotation(Marker.class);
        System.out.println("练习2类上可读=" + (marker != null));
        // 若写成：
        //   @Marker void bad() {}
        // 编译器会报：annotation type not applicable to this kind of declaration
        // （@Target 只有 TYPE，不能标方法）
        System.out.println("练习2标方法会编译失败=true（见源码注释）");

        // —— 练习3 ——
        // 预测：仅有注解不会拦；反射读标记后再显式检查才拒绝 null/空白
        OrderInput ok = new OrderInput("C001");
        requireNonBlank(ok, "customerId");
        System.out.println("练习3非空通过=true");

        boolean nullRejected = false;
        try {
            requireNonBlank(new OrderInput(null), "customerId");
        } catch (IllegalArgumentException ex) {
            nullRejected = true;
        }
        boolean blankRejected = false;
        try {
            requireNonBlank(new OrderInput("  "), "customerId");
        } catch (IllegalArgumentException ex) {
            blankRejected = true;
        }
        System.out.println("练习3拒绝null=" + nullRejected);
        System.out.println("练习3拒绝空白=" + blankRejected);
        System.out.println("练习3仅注解不校验=true（必须自己写反射检查）");
    }
}
