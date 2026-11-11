package learn.data.jdbc;

import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;

/**
 * 第37课三级练习（自敲，先预测再跑；H2 内存库，退出即消失；每题独立库）：
 * 1) 转 50 → 450/250；再失败转 99 后仍 450/250
 * 2) 转 501 → IllegalStateException 仍 500/200；转 99 → rollback 仍 500/200
 * 3) 注入式姓名原样存回；可选并发两转账不丢钱
 */
public final class JdbcExercises {
    private JdbcExercises() {}

    public static void main(String[] args) throws Exception {
        exercise1();
        exercise2();
        exercise3();
    }

    /** 练习1：模仿 — 成功转 50 分；再失败转给 id=99，回滚后仍是成功后余额 */
    static void exercise1() throws Exception {
        JdbcDataSource source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:jdbc_ex1");
        try (Connection keeper = source.getConnection()) {
            AccountRepository accounts = new AccountRepository(source);
            accounts.createSchema();
            accounts.insert(1, "小林", 500);
            accounts.insert(2, "阿青", 200);
            TransferService transfers = new TransferService(source);
            // 预测：500-50=450，200+50=250
            transfers.transfer(1, 2, 50);
            System.out.println("练习1成功后：小林=" + accounts.balance(1) + "，阿青=" + accounts.balance(2));
            try {
                transfers.transfer(1, 99, 50);
            } catch (IllegalArgumentException expected) {
                System.out.println("练习1失败原因：" + expected.getMessage());
            }
            // 预测：仍是 450/250（成功那笔已 commit，失败那笔 rollback）
            System.out.println("练习1回滚后：小林=" + accounts.balance(1) + "，阿青=" + accounts.balance(2));
        }
    }

    /** 练习2：修改 — 余额不足 501；收款不存在 id=99；余额都应不变 */
    static void exercise2() throws Exception {
        JdbcDataSource source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:jdbc_ex2");
        try (Connection keeper = source.getConnection()) {
            AccountRepository accounts = new AccountRepository(source);
            accounts.createSchema();
            accounts.insert(1, "小林", 500);
            accounts.insert(2, "阿青", 200);
            TransferService transfers = new TransferService(source);

            // 预测：IllegalStateException（付款不存在或余额不足），仍 500/200
            try {
                transfers.transfer(1, 2, 501);
                System.out.println("练习2-501：未抛异常（意外）");
            } catch (IllegalStateException expected) {
                System.out.println("练习2-501异常：" + expected.getClass().getSimpleName() + "：" + expected.getMessage());
            }
            System.out.println("练习2-501后：小林=" + accounts.balance(1) + "，阿青=" + accounts.balance(2));

            // 预测：IllegalArgumentException（收款账户不存在），rollback 后仍 500/200
            try {
                transfers.transfer(1, 99, 100);
                System.out.println("练习2-99：未抛异常（意外）");
            } catch (IllegalArgumentException expected) {
                System.out.println("练习2-99异常：" + expected.getClass().getSimpleName() + "：" + expected.getMessage());
            }
            System.out.println("练习2-99后：小林=" + accounts.balance(1) + "，阿青=" + accounts.balance(2));
        }
    }

    /** 练习3：独立 — 注入式姓名原样存回；并发两转账不丢钱 */
    static void exercise3() throws Exception {
        // 3a 注入姓名
        JdbcDataSource sourceA = new JdbcDataSource();
        sourceA.setURL("jdbc:h2:mem:jdbc_ex3a");
        try (Connection keeper = sourceA.getConnection()) {
            AccountRepository accounts = new AccountRepository(sourceA);
            accounts.createSchema();
            accounts.insert(1, "小林", 500);
            String payload = "张'; DROP TABLE accounts; --";
            accounts.insert(3, payload, 1);
            // 预测：原样输出 payload；小林余额仍可查到 500（表还在）
            System.out.println("练习3姓名：" + accounts.name(3));
            System.out.println("练习3小林余额：" + accounts.balance(1));
        }

        // 3b 并发：两付款各 500，收款 200；各转 100 → 400/400/400
        JdbcDataSource sourceB = new JdbcDataSource();
        sourceB.setURL("jdbc:h2:mem:jdbc_ex3b");
        try (Connection keeper = sourceB.getConnection()) {
            AccountRepository accounts = new AccountRepository(sourceB);
            accounts.createSchema();
            accounts.insert(1, "小林", 500);
            accounts.insert(2, "阿青", 200);
            accounts.insert(3, "小雨", 500);
            TransferService transfers = new TransferService(sourceB);
            Thread t1 = new Thread(() -> {
                try { transfers.transfer(1, 2, 100); }
                catch (Exception e) { throw new RuntimeException(e); }
            });
            Thread t2 = new Thread(() -> {
                try { transfers.transfer(3, 2, 100); }
                catch (Exception e) { throw new RuntimeException(e); }
            });
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            // 预测：原子加款，不会丢更新 → 400 / 400 / 400
            System.out.println("练习3并发后：小林=" + accounts.balance(1)
                    + "，阿青=" + accounts.balance(2)
                    + "，小雨=" + accounts.balance(3));
            System.out.println("练习3说明：入账用 balance_cents=balance_cents+?，库内原子加，并发不会基于旧余额覆盖成 300。");
        }
    }
}
