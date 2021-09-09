package com.concurrent.p5;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 同步模式-顺序控制
 */
@Slf4j(topic = "c.TestSyncModel_SequenceControl")
public class TestSyncModel_SequenceControl {
    /**
     * 先执行t2后执行t1
     * wait-notify()实现
     */
    //定义锁对象
    static final Object lock1 = new Object();
    //定义t2是否执行过的标识
    static boolean t2Finished1 = false;

    @Test
    public void test1() {

        //线程t1
        Thread t1 = new Thread(() -> {
            synchronized (lock1) {
                //如果t2没执行完，就一直等待
                while (!t2Finished1) {
                    try {
                        lock1.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //如果t2执行完毕，t1开始执行
                log.debug("running...");
            }
        }, "t1");
        t1.start();
        //线程t2
        Thread t2 = new Thread(() -> {
            synchronized (lock1) {
                log.debug("running...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                t2Finished1 = true;
                //t2执行完唤醒t1
                lock1.notify();
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

    /**
     * 顺序控制-ReentrantLock
     */
    static ReentrantLock lock2 = new ReentrantLock();
    static Condition condition2 = lock2.newCondition();
    static boolean t2Finished2 = false;

    @Test
    public void test2() {
        //线程t1
        Thread t1 = new Thread(() -> {
            try {
                lock2.lock();
                while (!t2Finished2) {
                    try {
                        condition2.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("running...");
            } finally {
                lock2.unlock();
            }
        }, "t1");
        t1.start();

        //线程t2
        Thread t2 = new Thread(() -> {
            try {
                lock2.lock();
                log.debug("running...");
                try {
                    Thread.sleep(3000);
                    t2Finished2 = true;
                    condition2.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                lock2.unlock();
            }
        }, "t2");
        t2.start();

        //等待t1，t2执行完
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * park,unpark版
     */
    @Test
    public void test3() {
        Thread t1 = new Thread(() -> {
            LockSupport.park();
            log.debug("running");
        }, "t1");
        t1.start();
        Thread t2 = new Thread(() -> {
            log.debug("running...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //唤醒t1线程
            LockSupport.unpark(t1);
        }, "t2");
        t2.start();

        //等待t1，t2执行完
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
