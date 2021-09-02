package com.concurrent.p2;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 临界区Critical Section
 * <p>
 * 一个线程运行多个线程本身是没有问题的，问题出现在多个线程访问共享资源。
 * 多个线程读共享资源是没有问题的
 * 在多个线程对共享资源读写操作时发生指令交错，就会出现问题。
 * <p>
 * 一段代码内如果存在对共享资源的多线程读写操作，次代码块称为临界区。
 * <p>
 * 竞态条件Race Condition
 * 多个线程在临界区内执行，由于代码的执行序列不同而导致结果无法预测，称之为发生了竞态条件
 */
@Slf4j(topic = "c.TestSynchronized")
public class TestSynchronized {

    /**
     * 测试在不同步的情况下读写共享变量
     */
    static int num = 0;

    @Test
    public void testUnSynchronized() {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    num++;  //临界区
                }
            }
        };
        t1.start();
        Thread t2 = new Thread("t2") {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    num++;  //临界区
                }
            }
        };
        t2.start();
        //等待t1,t2执行完
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //15:26:34.401 [main] DEBUG c.TestCriticalSection - num:13168
        //此结果不是预期的20000这是由于不同步造成的线程安全问题
        log.debug("num:{}", num);
    }

    //锁对象
    static Object object = new Object();

    @Test
    public void testSynchronized() {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    synchronized (object) {
                        num--;  //临界区
                    }
                }
            }
        };
        t1.start();
        Thread t2 = new Thread("t2") {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    synchronized (object) {
                        num++;  //临界区
                    }
                }
            }
        };
        t2.start();
        //等待t1,t2执行完
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //15:48:59.483 [main] DEBUG c.TestCriticalSection - num:0
        //线程同步后，结果正确
        log.debug("num:{}", num);
    }
    /**
     * synchronized实际上是用对象锁保证了临界区内代码的原子性。
     * 临界区内的代码对外是不可分隔的，不会被线程切换所打断。
     */

    /**
     * synchronized加载for循环上
     */
    @Test
    public void testSynchronized_for() {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                synchronized (object) {     //会把整个for循环作为一个原子操作，但是粒度大
                    for (int i = 0; i < 10000; i++) {
                        num--;  //临界区
                    }
                }
            }
        };
        t1.start();
        Thread t2 = new Thread("t2") {
            @Override
            public void run() {
                synchronized (object) {     //会把整个for循环作为一个原子操作，但是粒度大
                    for (int i = 0; i < 10000; i++) {
                        num++;  //临界区
                    }
                }
            }
        };
        t2.start();
        //等待t1,t2执行完
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //15:48:59.483 [main] DEBUG c.TestCriticalSection - num:0
        //线程同步后，结果正确
        log.debug("num:{}", num);
    }

    /**
     * synchronized使用不同的锁
     */
    static Object obj1 = new Object();
    static Object obj2 = new Object();

    @Test
    public void testSynchronized_differentLock() {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    synchronized (obj1) {
                        num--;  //临界区
                    }
                }
            }
        };
        t1.start();
        Thread t2 = new Thread("t2") {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    synchronized (obj2) {
                        num++;  //临界区
                    }
                }
            }
        };
        t2.start();
        //等待t1,t2执行完
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //16:07:44.494 [main] DEBUG c.TestCriticalSection - num:769
        //使用不同的锁对象，线程不会同步，结果错误
        log.debug("num:{}", num);
    }

    /**
     * t1加了synchronized，t2没有加，不会同步
     */
    @Test
    public void testSynchronized_t1() {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    synchronized (object) {
                        num--;  //临界区
                    }
                }
            }
        };
        t1.start();
        Thread t2 = new Thread("t2") {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    num++;  //临界区
                }
            }
        };
        t2.start();
        //等待t1,t2执行完
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //16:09:42.638 [main] DEBUG c.TestCriticalSection - num:475
        //错误
        log.debug("num:{}", num);
    }
}
