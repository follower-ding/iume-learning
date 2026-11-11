package learn.lesson03_operators;

/** 练习1：17分钟含几个完整5分钟、还剩几分钟 */
public class MinutesProbe {
    public static void main(String[] args) {
        int minutes = 17;
        System.out.println("完整5分钟个数=" + minutes / 5);
        System.out.println("剩余分钟=" + minutes % 5);
    }
}
