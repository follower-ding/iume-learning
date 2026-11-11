package learn.lesson06_loops;

/** 练习3：九九乘法表；外层行号，内层该行算式个数 */
public class TimesTableProbe {
    public static void main(String[] args) {
        for (int row = 1; row <= 9; row++) {
            for (int column = 1; column <= row; column++) {
                System.out.print(column + "×" + row + "=" + (column * row) + "\t");
            }
            System.out.println();
        }
    }
}
