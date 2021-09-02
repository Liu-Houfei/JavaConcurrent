package com.concurrent.p1;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Java中线程6状态
 */
@Slf4j(topic = "c.TestThreadStatus")
public class TestThreadStatus {

    @Test
    public void testJava_6Status() {
        //线程1，正常执行
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                log.debug("running...");
            }
        };      //NEW

        //线程2，死循环，一直占用cpu
        Thread t2 = new Thread("t2") {
            @Override
            public void run() {
                while (true) {

                }
            }
        };
        t2.start();     //RUNNABLE
        //线程3，执行完一行代码就结束
        Thread t3 = new Thread("t3") {
            @Override
            public void run() {
                log.debug("running...");
            }
        };
        t3.start();     //TERMINATED
        //线程4，有时间的等待sleep
        Thread t4 = new Thread("t4") {
            @Override
            public void run() {
                synchronized (TestThreadStatus.class) {
                    try {
                        Thread.sleep(1000000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t4.start();     //TIMED_WAITING
        //线程5，线程2运行不完就一直等待
        Thread t5 = new Thread("t5") {
            @Override
            public void run() {
                try {
                    t2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t5.start();     //WAITING
        //线程6，获取不到锁就一直阻塞
        Thread t6 = new Thread("t6") {
            @Override
            public void run() {
                synchronized (TestThreadStatus.class) {
                    try {
                        Thread.sleep(1000000000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t6.start();     //BLOCKED

        log.debug(t1.getState() + "");      //NEW
        log.debug(t2.getState() + "");      //RUNNABLE
        log.debug(t3.getState() + "");      //TERMINATED
        log.debug(t4.getState() + "");      //TIMED_WAITING
        log.debug(t5.getState() + "");      //WAITING
        log.debug(t6.getState() + "");      //BLOCKED
    }
}
