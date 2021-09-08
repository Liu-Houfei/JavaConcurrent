package com.concurrent.p4;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j(topic = "c.TestDeadLock")
public class TestDeadLock {

    static final Object A = new Object();
    static final Object B = new Object();

    @Test
    public void testDeadLock() {
        //线程1获得A锁，又想获得B锁
        Thread t1 = new Thread(() -> {
            synchronized (A) {
                log.debug("获得A锁...");
                synchronized (B) {
                    log.debug("获得B锁...");
                }
            }
        }, "t1");
        t1.start();
        //线程2获得B锁，又想获得A锁
        Thread t2 = new Thread(() -> {
            synchronized (B) {
                log.debug("获得B锁...");
                synchronized (A) {
                    log.debug("获得A锁...");
                }
            }
        }, "t2");
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
