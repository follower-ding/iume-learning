package learn.data.jdbc;

import javax.sql.DataSource;
import java.sql.*;

public final class AccountRepository {
    private final DataSource source;
    public AccountRepository(DataSource source) { this.source = source; }

    public void createSchema() throws SQLException {
        try (Connection connection = source.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE accounts(id BIGINT PRIMARY KEY, name VARCHAR(100) NOT NULL, balance_cents INT NOT NULL CHECK(balance_cents >= 0))");
        }
    }

    public void insert(long id, String name, int cents) throws SQLException {
        // ? 占位符让引号和 SQL 关键字按普通数据处理。
        try (Connection connection = source.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO accounts(id,name,balance_cents) VALUES(?,?,?)")) {
            statement.setLong(1, id);
            statement.setString(2, name);
            statement.setInt(3, cents);
            statement.executeUpdate();
        }
    }

    public int balance(long id) throws SQLException {
        try (Connection connection = source.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT balance_cents FROM accounts WHERE id=?")) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) throw new IllegalArgumentException("账户不存在: " + id);
                return result.getInt(1);
            }
        }
    }

    public String name(long id) throws SQLException {
        try (Connection connection = source.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT name FROM accounts WHERE id=?")) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) throw new IllegalArgumentException("账户不存在: " + id);
                return result.getString(1);
            }
        }
    }
}
