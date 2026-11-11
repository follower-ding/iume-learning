package learn.lesson03_operators;

/** 练习3：年龄 18~60 且持票 → 能否进入 */
public class CanEnterProbe {
    public static void main(String[] args) {
        int age = 20;
        boolean hasTicket = true;
        boolean canEnter = age >= 18 && age <= 60 && hasTicket;
        System.out.println("age=20,hasTicket=true → 可以进入=" + canEnter);

        age = 17;
        canEnter = age >= 18 && age <= 60 && hasTicket;
        System.out.println("age=17,hasTicket=true → 可以进入=" + canEnter);

        age = 20;
        hasTicket = false;
        canEnter = age >= 18 && age <= 60 && hasTicket;
        System.out.println("age=20,hasTicket=false → 可以进入=" + canEnter);
    }
}
