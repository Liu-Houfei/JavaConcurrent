package com.concurrent.p2;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

@Slf4j(topic = "c.TestBiasedLock2")
public class TestBiasedLock2 {

    /**
     * 创建对象，101表示可以使用偏向锁
     * 如果要避免延时加载，加JVM命令
     * -XX:BiasedLockingStartupDelay=0
     */
    @Test
    public void t1() throws InterruptedException {
        //markword
        log.debug(ClassLayout.parseInstance(new Dog()).toPrintable());
        //延时4秒
        Thread.sleep(4000);
        log.debug(ClassLayout.parseInstance(new Dog()).toPrintable());
    }

    /**
     * 加上偏向锁
     */
    @Test
    public void t2() {
        Dog d = new Dog();
        //加锁前
        log.debug(ClassLayout.parseInstance(d).toPrintable());
        //加锁中
        synchronized (d) {
            log.debug(ClassLayout.parseInstance(d).toPrintable());
        }
        //加锁后
        log.debug(ClassLayout.parseInstance(d).toPrintable());
    }

    /**
     * 禁用偏向锁
     * JVM参数  -XX:-UseBiasedLocking
     */
    @Test
    public void t3() {
        Dog d = new Dog();
        //加锁前
        log.debug(ClassLayout.parseInstance(d).toPrintable());
        //加锁中
        synchronized (d) {
            log.debug(ClassLayout.parseInstance(d).toPrintable());
        }
        //加锁后
        log.debug(ClassLayout.parseInstance(d).toPrintable());
    }

    /**
     * 同步前调用对象的 hashCode()方法
     * 偏向锁转为轻量级锁
     * 关闭延时加载    -XX:BiasedLockingStartupDelay=0
     */
    @Test
    public void t4() {
        Dog d = new Dog();
        d.hashCode();   //会禁用对象的偏向锁
        //加锁前
        log.debug(ClassLayout.parseInstance(d).toPrintable());
        //加锁中
        synchronized (d) {
            log.debug(ClassLayout.parseInstance(d).toPrintable());
        }
        //加锁后
        log.debug(ClassLayout.parseInstance(d).toPrintable());
    }

    /**
     * 撤销-其他线程使用偏向锁对象
     * 偏向锁转为轻量级锁
     * 关闭延时加载    -XX:BiasedLockingStartupDelay=0
     */
    static final Object lock = new Object();

    @Test
    public void t5() throws InterruptedException {
        Dog d = new Dog();
        new Thread(() -> {
            log.debug(ClassLayout.parseInstance(d).toPrintable());
            synchronized (d) {
                log.debug(ClassLayout.parseInstance(d).toPrintable());
            }
            log.debug(ClassLayout.parseInstance(d).toPrintable());
            synchronized (lock) {
                lock.notify();
            }
        }, "t1").start();
        new Thread(() -> {
            synchronized (lock) { //把两个线程分开
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug(ClassLayout.parseInstance(d).toPrintable());
            synchronized (d) {
                log.debug(ClassLayout.parseInstance(d).toPrintable());
            }
            log.debug(ClassLayout.parseInstance(d).toPrintable());
        }, "t2").start();

        Thread.sleep(10000);
    }

    /**
     * 撤销-wait/notify
     * 偏向锁转为重量级锁
     * 关闭延时加载    -XX:BiasedLockingStartupDelay=0
     */
    @Test
    public void t6() throws InterruptedException {
        Dog d = new Dog();
        //线程1
        new Thread(() -> {
            log.debug(ClassLayout.parseInstance(d).toPrintable());
            synchronized (d) {
                try {
                    d.wait();   //线程t1等待
                    log.debug(ClassLayout.parseInstance(d).toPrintable());
                    log.debug("线程被唤醒....");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug(ClassLayout.parseInstance(d).toPrintable());
        }, "t1").start();
        //线程2
        new Thread(() -> {
            log.debug(ClassLayout.parseInstance(d).toPrintable());
            synchronized (d) {
                try {
                    Thread.sleep(1000); //1000秒后唤醒线程t1
                    log.debug(ClassLayout.parseInstance(d).toPrintable());
                    d.notify();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            log.debug(ClassLayout.parseInstance(d).toPrintable());
        }, "t2").start();

        Thread.sleep(10000);
    }
}

class Dog {

}