package learn.data.jdbc;

import org.h2.jdbcx.JdbcDataSource;
import java.sql.Connection;

public final class JdbcDemo {
    private JdbcDemo() {}
    public static void main(String[] args) throws Exception {
        JdbcDataSource source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:transfer_demo");
        // 内存库需有一个连接保持存活，其他短连接才能看到同一张表。
        try (Connection keeper = source.getConnection()) {
            AccountRepository accounts = new AccountRepository(source);
            accounts.createSchema();
            accounts.insert(1, "小林", 500);
            accounts.insert(2, "阿青", 200);
            TransferService transfers = new TransferService(source);
            transfers.transfer(1, 2, 100);
            System.out.println("成功后：小林=" + accounts.balance(1) + "，阿青=" + accounts.balance(2));
            try { transfers.transfer(1, 99, 50); }
            catch (IllegalArgumentException expected) { System.out.println("失败原因：" + expected.getMessage()); }
            System.out.println("回滚后：小林=" + accounts.balance(1) + "，阿青=" + accounts.balance(2));
        }
    }
}
