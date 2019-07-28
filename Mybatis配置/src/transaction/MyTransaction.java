package transaction;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.jdbc.JdbcTransaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务管理器主要作用：
 * 提交、回滚、关闭事务
 */
public class MyTransaction extends JdbcTransaction implements Transaction {

    public MyTransaction(Connection connection){
        super(connection);
    }
    public MyTransaction(DataSource dataSource, TransactionIsolationLevel level,
                         boolean desiredAutoCommit){
        super(dataSource, level, desiredAutoCommit);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return super.getConnection();
    }

    @Override
    public void commit() throws SQLException {
        super.commit();
    }

    @Override
    public void rollback() throws SQLException {
        super.rollback();
    }

    @Override
    public void close() throws SQLException {
        super.close();
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return super.getTimeout();
    }
}
