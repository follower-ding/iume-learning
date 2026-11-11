package learn.lesson14_abstract;

public class Rectangle extends Shape {
    private int width;
    private int height;

    public Rectangle(int width, int height) {
        super("矩形");
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("边长不能为负数");
        }
        this.width = width;
        this.height = height;
    }

    // @Override 实现 Shape 的面积约定；先转 long 再乘，避免 int 溢出
    @Override
    public long area() {
        return (long) width * height;
    }
}
