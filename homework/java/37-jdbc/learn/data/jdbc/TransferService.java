package learn.data.jdbc;

import javax.sql.DataSource;
import java.sql.*;

public final class TransferService {
    private final DataSource source;
    public TransferService(DataSource source) { this.source = source; }

    public void transfer(long fromId, long toId, int amountInCents) throws SQLException {
        if (amountInCents <= 0 || fromId == toId) throw new IllegalArgumentException("金额必须为正且账户不同");
        try (Connection connection = source.getConnection()) {
            // 同一连接关闭自动提交，两个 UPDATE 才会属于一个事务。
            connection.setAutoCommit(false);
            try {
                int changed;
                try (PreparedStatement debit = connection.prepareStatement(
                        "UPDATE accounts SET balance_cents=balance_cents-? WHERE id=? AND balance_cents>=?")) {
                    debit.setInt(1, amountInCents);
                    debit.setLong(2, fromId);
                    debit.setInt(3, amountInCents);
                    changed = debit.executeUpdate();
                }
                if (changed != 1) throw new IllegalStateException("付款账户不存在或余额不足");
                // 在数据库内原子加款；上界条件防止 int 分值溢出，并避免并发读后覆盖。
                try (PreparedStatement credit = connection.prepareStatement(
                        "UPDATE accounts SET balance_cents=balance_cents+? WHERE id=? AND balance_cents<=?")) {
                    credit.setInt(1, amountInCents);
                    credit.setLong(2, toId);
                    credit.setInt(3, Integer.MAX_VALUE - amountInCents);
                    if (credit.executeUpdate() != 1) {
                        try (PreparedStatement query = connection.prepareStatement("SELECT id FROM accounts WHERE id=?")) {
                            query.setLong(1, toId);
                            try (ResultSet result = query.executeQuery()) {
                                if (!result.next()) throw new IllegalArgumentException("收款账户不存在");
                                throw new ArithmeticException("收款账户余额溢出");
                            }
                        }
                    }
                }
                connection.commit();
            } catch (SQLException | RuntimeException failure) {
                try { connection.rollback(); }
                catch (SQLException rollbackFailure) { failure.addSuppressed(rollbackFailure); }
                throw failure;
            }
        }
    }
}
