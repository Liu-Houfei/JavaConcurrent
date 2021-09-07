package com.concurrent.p3;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j(topic = "c.TestSleepWait")
public class TestSleepWait {

    //定义锁对象
    static final Object obj = new Object();


    /**
     * sleep()、interrupt()
     * sleep不会释放锁
     */
    @Test
    public void t1() {
        //线程1
        Thread t1 = new Thread(() -> {
            synchronized (obj) {
                log.debug("开始执行同步代码块");
                log.debug("开始sleep");
                try {
                    //sleep时，线程不会释放锁
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("睡眠结束，执行其他代码");
            }
        }, "t1");
        t1.start();

        //线程2
        Thread t2 = new Thread(() -> {
            //唤醒t1的sleep
            t1.interrupt();
            synchronized (obj) {
                log.debug("开始执行同步代码块");
                log.debug("睡眠结束，执行其他代码");
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
