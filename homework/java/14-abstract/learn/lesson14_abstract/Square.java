package learn.lesson14_abstract;

// 练习3：独立——正方形 extends Shape，面积 = 边长 × 边长
public class Square extends Shape {
    private int side;

    public Square(int side) {
        super("正方形");
        if (side < 0) {
            throw new IllegalArgumentException("边长不能为负数");
        }
        this.side = side;
    }

    @Override
    public long area() {
        return (long) side * side;
    }
}
