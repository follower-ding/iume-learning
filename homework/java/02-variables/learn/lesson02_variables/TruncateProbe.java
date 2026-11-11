package learn.lesson02_variables;

/** 改一个值 + 体会 (int) 截断（不是四舍五入） */
public class TruncateProbe {
    public static void main(String[] args) {
        // 改：年龄起点改成 20，期望输出 21
        int age = 20;
        age = age + 1;
        System.out.println("改年龄起点后=" + age);

        // 体会截断：1.75→1，1.99→1，-1.75→-1（向零截断）
        System.out.println("(int)1.75=" + (int) 1.75);
        System.out.println("(int)1.99=" + (int) 1.99);
        System.out.println("(int)-1.75=" + (int) -1.75);
    }
}
