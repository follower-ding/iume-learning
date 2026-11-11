package learn.lesson29_annotations;

/** 被标记的演示对象；读取注解不增加计数，调用方法才增加。 */
public final class AnnotationDemo {
    private int invocationCount;

    @DemoCase("annotation metadata")
    public void annotatedExample() {
        invocationCount++;
    }

    public int invocationCount() {
        return invocationCount;
    }
}
