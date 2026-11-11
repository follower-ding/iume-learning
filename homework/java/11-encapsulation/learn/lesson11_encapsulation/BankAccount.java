package learn.lesson11_encapsulation;

public class BankAccount {
    // private 阻止外部跳过校验直接改余额。
    private int balance;

    public void deposit(int amount) {
        if (amount <= 0 || amount > Integer.MAX_VALUE - balance) {
            throw new IllegalArgumentException("存款金额无效或超出余额范围");
        }
        balance += amount;
    }

    public void withdraw(int amount) {
        if (amount <= 0 || amount > balance) {
            throw new IllegalArgumentException("取款金额无效");
        }
        balance -= amount;
    }

    public int balance() {
        return balance;
    }

    // 练习3：转账——先全盘校验，再改两边；失败时两边余额都不动
    public void transferTo(BankAccount target, int amount) {
        if (target == null) {
            throw new IllegalArgumentException("目标账户无效");
        }
        if (amount <= 0 || amount > balance) {
            throw new IllegalArgumentException("转账金额无效");
        }
        if (amount > Integer.MAX_VALUE - target.balance) {
            throw new IllegalArgumentException("目标账户无法容纳该金额");
        }
        // 校验都过了再动钱：先自己取、再对方存
        this.withdraw(amount);
        target.deposit(amount);
    }
}
