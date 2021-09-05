package com.concurrent.p2;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

import java.util.Vector;
import java.util.concurrent.locks.LockSupport;

@Slf4j(topic = "c.TestBiasedLock3")
public class TestBiasedLock3 {

    /**
     * 批量重偏向
     * 关闭延时加载    -XX:BiasedLockingStartupDelay=0
     */
    @Test
    public void t1() throws InterruptedException {
        Vector<Dog> list = new Vector<>();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 30; i++) {
                Dog d = new Dog();
                list.add(d);
                synchronized (d) {
                    log.debug(i + ":\t" + ClassLayout.parseInstance(d).toPrintable());
                }
            }
            synchronized (list) {
                list.notifyAll();
            }
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            synchronized (list) {
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.debug("=========");
            for (int i = 0; i < 30; i++) {
                Dog d = list.get(i);
                log.debug(i + ":\t" + ClassLayout.parseInstance(d).toPrintable());
                synchronized (d) {  //批量重偏向
                    log.debug(i + ":\t" + ClassLayout.parseInstance(d).toPrintable());
                }
            }
        }, "t2");
        t2.start();

        Thread.sleep(10000);
    }

    /**
     * 批量撤销
     * 关闭延时加载    -XX:BiasedLockingStartupDelay=0
     */
    static Thread t1, t2, t3;

    @Test
    public void t2() {
        Vector<Dog> list = new Vector<>();
        long loopNumber = 39;
        //线程1
        t1 = new Thread(() -> {
            for (int i = 0; i < loopNumber; i++) {
                Dog d = new Dog();
                synchronized (d) {
                    log.debug(i + ":\t" + ClassLayout.parseInstance(d).toPrintable());
                }
                list.add(d);
            }
            LockSupport.unpark(t2);
        }, "t1");
        t1.start();
        //线程2
        t2 = new Thread(() -> {
            LockSupport.park();
            log.debug("========================");
            for (int i = 0; i < loopNumber; i++) {
                Dog d = list.get(i);
                synchronized (d) {
                    log.debug(i + ":\t" + ClassLayout.parseInstance(d).toPrintable());
                }
            }
            LockSupport.unpark(t3);
        }, "t2");
        t2.start();
        //线程3
        t3 = new Thread(() -> {
            LockSupport.park();
            log.debug("========================");
            for (int i = 0; i < loopNumber; i++) {
                Dog d = list.get(i);
                synchronized (d) {
                    log.debug(i + ":\t" + ClassLayout.parseInstance(d).toPrintable());
                }
            }
        }, "t3");
        t3.start();
        try {
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //创建新对象，jvm认为竞争激烈，将整个类的对象都设置成不可偏向
        log.debug(ClassLayout.parseInstance(new Dog()).toPrintable());
    }
}