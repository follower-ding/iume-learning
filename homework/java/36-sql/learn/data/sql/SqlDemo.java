package learn.data.sql;

import org.h2.jdbcx.JdbcDataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class SqlDemo {
    private SqlDemo() {}

    public static void createAndSeed(Connection connection) throws SQLException {
        // DDL 定义表结构；外键阻止订单引用不存在的顾客。
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE customers(id INT PRIMARY KEY, name VARCHAR(80) NOT NULL, note VARCHAR(80))");
            statement.executeUpdate("CREATE TABLE orders(id INT PRIMARY KEY, customer_id INT NOT NULL REFERENCES customers(id), amount_cents INT NOT NULL CHECK(amount_cents >= 0))");
            statement.executeUpdate("INSERT INTO customers(id,name,note) VALUES(1,'小林',NULL),(2,'阿青','新客')");
            statement.executeUpdate("INSERT INTO orders(id,customer_id,amount_cents) VALUES(101,1,3000),(102,1,6000),(103,2,2500)");
        }
    }

    public static String ordersAtLeast(Connection connection, int minimumCents) throws SQLException {
        List<String> rows = new ArrayList<>();
        // PreparedStatement 的参数只表示值，不能改变 SQL 结构。
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id, amount_cents FROM orders WHERE amount_cents >= ? ORDER BY amount_cents DESC")) {
            statement.setInt(1, minimumCents);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) rows.add(result.getInt("id") + ":" + result.getInt("amount_cents"));
            }
        }
        return String.join("\n", rows);
    }
    public static String orderSummary(Connection connection) throws SQLException {
        String sql = """
                SELECT c.name, COUNT(o.id) AS order_count, SUM(o.amount_cents) AS total
                FROM customers c JOIN orders o ON c.id = o.customer_id
                GROUP BY c.id, c.name ORDER BY c.id
                """;
        List<String> rows = new ArrayList<>();
        try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(sql)) {
            while (result.next()) rows.add(result.getString("name") + ":" + result.getInt("order_count") + ":" + result.getInt("total"));
        }
        return String.join("\n", rows);
    }

    public static void main(String[] args) throws Exception {
        JdbcDataSource source = new JdbcDataSource();
        source.setURL("jdbc:h2:mem:sql_demo");
        try (Connection connection = source.getConnection()) {
            createAndSeed(connection);
            System.out.println("至少3000分："+ordersAtLeast(connection, 3000).replace("\n", "，"));
            System.out.println("订单汇总：");
            System.out.println(orderSummary(connection));
            // UPDATE、DELETE 真正改变表；在本地内存库中演示。
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("UPDATE customers SET note='回头客' WHERE id=1");
                statement.executeUpdate("DELETE FROM orders WHERE id=103");
            }
            System.out.println("删除后订单数：" + countOrders(connection));
        }
    }

    private static int countOrders(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM orders")) {
            result.next();
            return result.getInt(1);
        }
    }
}
