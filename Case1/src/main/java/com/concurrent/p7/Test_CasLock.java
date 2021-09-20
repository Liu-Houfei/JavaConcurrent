package com.concurrent.p7;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j(topic = "c.Test_CasLock")
public class Test_CasLock {

    @Test
    public void testCasLock() {
        log.debug("测试CAS锁");
        CasLock lock = new CasLock();
        Thread t1 = new Thread(() -> {
            lock.lock();
            log.debug("模拟操作");
            lock.unlock();
        }, "t1");
        t1.start();
        Thread t2 = new Thread(() -> {
            lock.lock();
            log.debug("模拟操作");
            lock.unlock();
        }, "t2");
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /**
         * 10:46:07.914 [main] DEBUG c.Test_CasLock - 测试CAS锁
         * 10:46:07.976 [t1] DEBUG c.CasLock - lock...
         * 10:46:07.977 [t2] DEBUG c.CasLock - lock...
         * 10:46:07.977 [t1] DEBUG c.Test_CasLock - 模拟操作
         * 10:46:07.979 [t1] DEBUG c.CasLock - unlock...
         * 10:46:07.979 [t2] DEBUG c.Test_CasLock - 模拟操作
         * 10:46:07.979 [t2] DEBUG c.CasLock - unlock...
         */
    }
}

@Slf4j(topic = "c.CasLock")
class CasLock {
    //定义一个标志，记录上锁的状态
    //0：没加锁
    //1：加锁
    private AtomicInteger status = new AtomicInteger(0);

    //cas方式实现加锁
    public void lock() {
        log.debug("lock...");
        do {

        } while (!status.compareAndSet(0, 1));
    }

    //解锁
    public void unlock() {
        log.debug("unlock...");
        status.set(0);
    }

}