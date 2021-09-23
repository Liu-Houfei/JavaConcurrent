package com.concurrent.p8;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicIntegerArray;

@Slf4j(topic = "c.Test_DBConnPool")
public class Test_DBConnPool {

    @Test
    public void test_ConnPool() {
        //创建连接池对象
        ConnPool pool = new ConnPool(2);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                Connection connection = pool.borrow();
                //模拟等待时间
                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //归还连接
                pool.free(connection);
            }).start();
        }
        while (true) ;
    }
    /**
     * 08:27:36.694 [Thread-1] DEBUG c.ConnPool - get conn...
     * 08:27:36.694 [Thread-0] DEBUG c.ConnPool - get conn...
     * 08:27:36.694 [Thread-2] DEBUG c.ConnPool - wait...
     * 08:27:36.698 [Thread-4] DEBUG c.ConnPool - wait...
     * 08:27:36.698 [Thread-3] DEBUG c.ConnPool - wait...
     * 08:27:36.899 [Thread-0] DEBUG c.ConnPool - free MyConnection{name='conn-0'}
     * 08:27:36.907 [Thread-3] DEBUG c.ConnPool - get conn...
     * 08:27:36.907 [Thread-4] DEBUG c.ConnPool - wait...
     * 08:27:36.907 [Thread-2] DEBUG c.ConnPool - wait...
     * 08:27:37.251 [Thread-1] DEBUG c.ConnPool - free MyConnection{name='conn-1'}
     * 08:27:37.251 [Thread-2] DEBUG c.ConnPool - get conn...
     * 08:27:37.251 [Thread-4] DEBUG c.ConnPool - wait...
     * 08:27:37.327 [Thread-3] DEBUG c.ConnPool - free MyConnection{name='conn-0'}
     * 08:27:37.327 [Thread-4] DEBUG c.ConnPool - get conn...
     * 08:27:37.547 [Thread-2] DEBUG c.ConnPool - free MyConnection{name='conn-1'}
     * 08:27:38.166 [Thread-4] DEBUG c.ConnPool - free MyConnection{name='conn-0'}
     */
}

/**
 * 自定义数据库连接池
 */
@Slf4j(topic = "c.ConnPool")
class ConnPool {
    //1:数据库连接池大小
    private int poolSize;
    //2:数据库连接池的状态
    private AtomicIntegerArray status;
    //3:数据库连接池对象
    private Connection[] conn;

    //4:构造方法
    public ConnPool(int poolSize) {
        this.poolSize = poolSize;
        this.status = new AtomicIntegerArray(poolSize);
        this.conn = new Connection[poolSize];
        for (int i = 0; i < poolSize; i++) {
            this.conn[i] = new MyConnection("conn-" + i );

        }
    }

    //5:借连接
    public Connection borrow() {
        while (true) {
            for (int i = 0; i < poolSize; i++) {
                if (status.get(i) == 0) {
                    //cas变更状态
                    if (status.compareAndSet(i, 0, 1)) {
                        log.debug("get conn...");
                        return conn[i];
                    }
                }
            }
            //如果遍历1次后没有空闲的连接，则进入等待
            synchronized (this) {
                try {
                    log.debug("wait...");
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //6:还连接
    public void free(Connection conn) {
        //归还连接时要检查该连接是否属于连接池
        for (int i = 0; i < poolSize; i++) {
            if (this.conn[i] == conn) {
                this.status.set(i, 0);
                //唤醒等待线程
                synchronized (this) {
                    log.debug("free {}", conn);
                    this.notifyAll();
                }
                break;
            }
        }

    }
}

class MyConnection implements Connection {

    private String name;

    public MyConnection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return "MyConnection{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public Statement createStatement() throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return null;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return null;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {

    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return false;
    }

    @Override
    public void commit() throws SQLException {

    }

    @Override
    public void rollback() throws SQLException {

    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {

    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {

    }

    @Override
    public String getCatalog() throws SQLException {
        return null;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {

    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return 0;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

    }

    @Override
    public void setHoldability(int holdability) throws SQLException {

    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return null;
    }

    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {

    }

    @Override
    public String getSchema() throws SQLException {
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {

    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}