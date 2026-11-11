package learn.lesson11_encapsulation;

public class EncapsulationDemo {
    public static void main(String[] args) {
        // ===== 讲义正文（期望两行）=====
        BankAccount account = new BankAccount();
        account.deposit(100);
        account.withdraw(40);
        System.out.println("余额=" + account.balance());
        try {
            account.withdraw(100);
        } catch (IllegalArgumentException exception) {
            System.out.println("超额取款被拒绝，余额=" + account.balance());
        }

        // ===== 练习1：模仿——存入20后查看余额 =====
        BankAccount a1 = new BankAccount();
        a1.deposit(20);
        System.out.println("练习1余额=" + a1.balance());

        // ===== 练习2：修改——取出0，拒绝后余额不变 =====
        BankAccount a2 = new BankAccount();
        a2.deposit(50);
        try {
            a2.withdraw(0);
        } catch (IllegalArgumentException exception) {
            System.out.println("练习2拒绝取0，余额=" + a2.balance());
        }

        // ===== 练习3：独立——transferTo 校验 =====
        BankAccount from = new BankAccount();
        BankAccount to = new BankAccount();
        from.deposit(80);
        to.deposit(10);
        from.transferTo(to, 30);
        System.out.println("练习3转账后 from=" + from.balance() + " to=" + to.balance());
        try {
            from.transferTo(null, 10);
        } catch (IllegalArgumentException exception) {
            System.out.println("练习3空目标被拒，from=" + from.balance() + " to=" + to.balance());
        }
        try {
            from.transferTo(to, 0);
        } catch (IllegalArgumentException exception) {
            System.out.println("练习3金额0被拒，from=" + from.balance() + " to=" + to.balance());
        }
        try {
            from.transferTo(to, 999);
        } catch (IllegalArgumentException exception) {
            System.out.println("练习3超额被拒，from=" + from.balance() + " to=" + to.balance());
        }
    }
}
