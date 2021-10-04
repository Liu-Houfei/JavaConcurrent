package com.concurrent.p10;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * 使用Semaphore改进数据库连接池
 */
@Slf4j(topic = "c.Test_SemaphoreApplication")
public class Test_SemaphoreApplication {

    class MyConn implements Connection {

        private String connName;

        public MyConn(String connName) {
            this.connName = connName;
        }

        @Override
        public String toString() {
            return "MyConn{" +
                    "connName='" + connName + '\'' +
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

    class Pool {
        //1.最大连接数
        private final int poolSize;
        //2.数据库连接数组
        private Connection connection[];
        //3.连接状态
        private AtomicIntegerArray status;
        //4.信号量
        private Semaphore semaphore;

        public Pool(int poolSize) {
            this.poolSize = poolSize;
            this.connection = new Connection[poolSize];
            this.status = new AtomicIntegerArray(new int[poolSize]);
            this.semaphore = new Semaphore(poolSize);
            for (int i = 0; i < poolSize; i++) {
                this.connection[i] = new MyConn("conn---" + i);
            }
        }

        //5.借连接
        public Connection borrow() {
            for (; ; ) {
                for (int i = 0; i < poolSize; i++) {
                    //有可用连接,则借出
                    if (status.get(i) == 0) {
                        status.set(i, 1);
                        return connection[i];
                    }
                }
                //遍历完一遍如果没有可用连接则等待
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //5.使用Semaphore改进借连接
        public Connection borrow_semaphore() {
            //获取许可
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < poolSize; i++) {
                //如果有空闲连接,则借出
                if (status.compareAndSet(i, 0, 1)) {  //此处要用CAS
                    return connection[i];
                }
            }
            return null;
        }

        //6.还连接
        public void free(Connection connection) {
            for (int i = 0; i < poolSize; i++) {
                if (this.connection[i] == connection) {
                    status.set(i, 0);
                    synchronized (this) {
                        this.notifyAll();
                    }
                    break;
                }
            }
        }

        //6.使用Semaphore改进还连接
        public void free_semaphore(Connection connection) {
            for (int i = 0; i < poolSize; i++) {
                if (this.connection[i] == connection) {
                    //使用了semaphore,不需要使用notifyAll唤醒其他线程
                    status.set(i, 0);
                    semaphore.release();
                    break;
                }
            }
        }
    }

    /**
     * 测试:wait/notify实现借还连接
     * <p>
     * 15:58:56.646 [Thread-1] DEBUG c.Test_SemaphoreApplication - borrow conn MyConn{connName='conn---1'}
     * 15:58:56.646 [Thread-2] DEBUG c.Test_SemaphoreApplication - borrow conn MyConn{connName='conn---2'}
     * 15:58:56.646 [Thread-0] DEBUG c.Test_SemaphoreApplication - borrow conn MyConn{connName='conn---0'}
     * 15:59:01.651 [Thread-1] DEBUG c.Test_SemaphoreApplication - free conn MyConn{connName='conn---1'}
     * 15:59:01.651 [Thread-3] DEBUG c.Test_SemaphoreApplication - borrow conn MyConn{connName='conn---2'}
     * 15:59:01.651 [Thread-2] DEBUG c.Test_SemaphoreApplication - free conn MyConn{connName='conn---2'}
     * 15:59:01.651 [Thread-4] DEBUG c.Test_SemaphoreApplication - borrow conn MyConn{connName='conn---1'}
     * 15:59:01.652 [Thread-0] DEBUG c.Test_SemaphoreApplication - free conn MyConn{connName='conn---0'}
     * 15:59:06.652 [Thread-4] DEBUG c.Test_SemaphoreApplication - free conn MyConn{connName='conn---1'}
     * 15:59:06.652 [Thread-3] DEBUG c.Test_SemaphoreApplication - free conn MyConn{connName='conn---2'}
     */
    @Test
    public void test1() {
        Pool connPool = new Pool(3);
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            threadList.add(new Thread(() -> {
                Connection connection = connPool.borrow();
                log.debug("borrow conn {}", connection);
                //5秒后释放连接
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connPool.free(connection);
                log.debug("free conn {}", connection);
            }));
        }

        threadList.forEach((s) -> s.start());
        threadList.forEach((s) -> {
            try {
                s.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 测试:Semaphore实现借还连接
     * 16:21:04.845 [Thread-1] DEBUG c.Test_SemaphoreApplication - borrow conn MyConn{connName='conn---0'}
     * 16:21:04.845 [Thread-0] DEBUG c.Test_SemaphoreApplication - borrow conn MyConn{connName='conn---1'}
     * 16:21:04.845 [Thread-2] DEBUG c.Test_SemaphoreApplication - borrow conn MyConn{connName='conn---2'}
     * 16:21:09.851 [Thread-1] DEBUG c.Test_SemaphoreApplication - free conn MyConn{connName='conn---0'}
     * 16:21:09.851 [Thread-4] DEBUG c.Test_SemaphoreApplication - borrow conn MyConn{connName='conn---1'}
     * 16:21:09.851 [Thread-3] DEBUG c.Test_SemaphoreApplication - borrow conn MyConn{connName='conn---0'}
     * 16:21:09.851 [Thread-0] DEBUG c.Test_SemaphoreApplication - free conn MyConn{connName='conn---1'}
     * 16:21:09.852 [Thread-2] DEBUG c.Test_SemaphoreApplication - free conn MyConn{connName='conn---2'}
     * 16:21:14.852 [Thread-4] DEBUG c.Test_SemaphoreApplication - free conn MyConn{connName='conn---1'}
     * 16:21:14.852 [Thread-3] DEBUG c.Test_SemaphoreApplication - free conn MyConn{connName='conn---0'}
     */
    @Test
    public void test2() {
        Pool connPool = new Pool(3);
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            threadList.add(new Thread(() -> {
                Connection connection = connPool.borrow_semaphore();
                log.debug("borrow conn {}", connection);
                //5秒后释放连接
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connPool.free_semaphore(connection);
                log.debug("free conn {}", connection);
            }));
        }

        threadList.forEach((s) -> s.start());
        threadList.forEach((s) -> {
            try {
                s.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


}