package com.concurrent.p4;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j(topic = "c.TestMultiLock")
public class TestMultiLock {

    /**
     * 把锁加在大对象上，并法程度低
     */
    @Test
    public void test1() {
        //BigRoom bigRoom = new BigRoom();
        BigRoom2 bigRoom = new BigRoom2();
        Thread t1 = new Thread(() -> {
            bigRoom.sleep();
        }, "t1");
        t1.start();
        Thread t2 = new Thread(() -> {
            bigRoom.study();
        }, "t2");
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            e.printStackTrace();
        }
    }
}

/**
 * 直接锁大对象
 */
@Slf4j(topic = "c.BigRoom")
class BigRoom {
    public void sleep() {
        synchronized (this) {
            log.debug("睡觉2小时");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void study() {
        synchronized (this) {
            log.debug("学习3小时");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * 优化
 * 加2个不同的锁
 */
@Slf4j(topic = "c.BigRoom2")
class BigRoom2 {
    private static final Object studyRoom = new Object();
    private static final Object sleepRoom = new Object();

    public void sleep() {
        synchronized (studyRoom) {
            log.debug("睡觉2小时");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void study() {
        synchronized (sleepRoom) {
            log.debug("学习3小时");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
