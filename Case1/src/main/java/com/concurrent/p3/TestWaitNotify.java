package com.concurrent.p3;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j(topic = "c.TestWaitNotify")
public class TestWaitNotify {

    //定义锁对象
    static final Object obj = new Object();

    @Test
    public void t1() throws InterruptedException {
        //线程1
        Thread t1 = new Thread(() -> {
            synchronized (obj) {
                log.debug("执行同步代码块");
                try {
                    obj.wait();     //让线程一直在obj上等待下去
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其他代码");
            }
        }, "t1");
        t1.start();
        //线程2
        Thread t2 = new Thread(() -> {
            synchronized (obj) {
                log.debug("执行同步代码块");
                try {
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其他代码");
            }
        }, "t2");
        t2.start();
        //主线程2秒后执行
        Thread.sleep(2000);
        log.debug("唤醒obj上其他线程");
        synchronized (obj) {
            //obj.notify();  //唤醒obj上的一个线程
            obj.notifyAll(); //唤醒obj上所有的线程
        }
        //等待t1、t2执行完成
        t1.join();
        t2.join();
    }
    /**
     * 20:56:34.847 [t1] DEBUG c.TestWaitNotify - 执行同步代码块
     * 20:56:34.862 [t2] DEBUG c.TestWaitNotify - 执行同步代码块
     * 20:56:36.815 [main] DEBUG c.TestWaitNotify - 唤醒obj上其他线程
     * 20:56:36.815 [t2] DEBUG c.TestWaitNotify - 其他代码
     * 20:56:36.815 [t1] DEBUG c.TestWaitNotify - 其他代码
     */


    /**
     * 有参数 wait(time)
     */
    @Test
    public void t2() {
        Thread t1 = new Thread(() -> {
            synchronized (obj) {
                log.debug("开始执行同步代码块");
                try {
                    //1秒后自动唤醒
                    obj.wait(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("执行其他代码");
            }
        }, "t1");
        t1.start();
        //等待t1执行完
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * 21:07:02.627 [t1] DEBUG c.TestWaitNotify - 开始执行同步代码块
     * 21:07:04.631 [t1] DEBUG c.TestWaitNotify - 执行其他代码
     */
}
