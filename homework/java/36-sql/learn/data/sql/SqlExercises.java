package learn.data.sql;

import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 第36课三级练习（自敲，先预测再跑；H2 内存库，退出即消失）：
 * 1) ordersAtLeast(5000) → 102:6000
 * 2) 加顾客小雨 + LEFT JOIN 汇总 → 小林2 / 阿青1 / 小雨0
 * 3) note IS NULL；外键插入 customer_id=99 → SQLException
 *
 * 均在 UPDATE/DELETE 之前的初始数据上练习。
 */
public final class SqlExercises {
    private SqlExercises() {}

    public static void main(String[] args) throws Exception {
        exercise1();
        exercise2();
        exercise3();
    }

    /** 练习1：模仿 — ordersAtLeast(5000)，预测只剩 102:6000 */
    static void exercise1() throws Exception {
        JdbcDataSource source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:sql_ex1");
        try (Connection connection = source.getConnection()) {
            SqlDemo.createAndSeed(connection);
            String rows = SqlDemo.ordersAtLeast(connection, 5000).replace("\n", "，");
            // 预测：102:6000（101 的 3000 被过滤）
            System.out.println("练习1：至少5000分：" + rows);
        }
    }

    /** 练习2：修改 — 插入无订单顾客「小雨」，LEFT JOIN 汇总全部顾客 */
    static void exercise2() throws Exception {
        JdbcDataSource source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:sql_ex2");
        try (Connection connection = source.getConnection()) {
            SqlDemo.createAndSeed(connection);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("INSERT INTO customers(id,name,note) VALUES(3,'小雨',NULL)");
            }
            String sql = """
                    SELECT c.name, COUNT(o.id) AS order_count
                    FROM customers c LEFT JOIN orders o ON c.id = o.customer_id
                    GROUP BY c.id, c.name ORDER BY c.id
                    """;
            List<String> rows = new ArrayList<>();
            try (Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery(sql)) {
                while (result.next()) {
                    rows.add(result.getString("name") + ":" + result.getInt("order_count"));
                }
            }
            // 预测：小林:2、阿青:1、小雨:0（LEFT JOIN 保留无订单顾客；COUNT(o.id) 不计 NULL）
            System.out.println("练习2：LEFT JOIN 汇总：");
            System.out.println(String.join("\n", rows));
        }
    }

    /** 练习3：独立 — note IS NULL；插入 customer_id=99 应外键失败 */
    static void exercise3() throws Exception {
        JdbcDataSource source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:sql_ex3");
        try (Connection connection = source.getConnection()) {
            SqlDemo.createAndSeed(connection);
            // 原始数据：仅小林 note 为 NULL
            List<String> nullNotes = new ArrayList<>();
            try (Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery(
                         "SELECT id, name FROM customers WHERE note IS NULL ORDER BY id")) {
                while (result.next()) {
                    nullNotes.add(result.getInt("id") + "," + result.getString("name"));
                }
            }
            System.out.println("练习3a：note IS NULL → " + String.join("；", nullNotes));

            String fkOutcome;
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO orders(id,customer_id,amount_cents) VALUES(?,?,?)")) {
                statement.setInt(1, 9);
                statement.setInt(2, 99);
                statement.setInt(3, 1);
                statement.executeUpdate();
                fkOutcome = "意外成功（不应发生）";
            } catch (SQLException e) {
                // 预测：外键约束失败；id=99 不在 customers
                fkOutcome = "SQLException:" + e.getClass().getSimpleName()
                        + "（外键拒绝 customer_id=99）";
            }
            System.out.println("练习3b：插入 id=99 → " + fkOutcome);
        }
    }
}
