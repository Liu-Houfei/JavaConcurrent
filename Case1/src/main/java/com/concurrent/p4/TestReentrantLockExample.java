package com.concurrent.p4;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用ReentrantLock改写送烟，送外卖的例子
 */

@Slf4j(topic = "c.TestReentrantLockExample")
public class TestReentrantLockExample {
    //static final Object room = new Object();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;
    //定义ReentrantLock对象，true表示该锁为公平锁
    static ReentrantLock lock = new ReentrantLock(true);
    //定义两个条件变量
    //吸烟室
    static Condition smokingRoom = lock.newCondition();
    //用餐室
    static Condition launchRoom = lock.newCondition();

    @Test
    public void test1() throws InterruptedException {
        //小南线程
        Thread t1 = new Thread(() -> {
            try {
                lock.lock();
                log.debug("有烟没？[{}]", hasCigarette);
                //将if改为while多次判断
                while (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    //没有烟就在吸烟室等待，此时可以释放锁
                    try {
                        smokingRoom.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没烟干不了");
                }
            } finally {
                lock.unlock();
            }

        }, "小南");
        t1.start();

        //小女线程
        Thread t2 = new Thread(() -> {
            try {
                lock.lock();
                log.debug("有外卖没？[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    //没有外卖就在用餐室等待，此时可以释放锁
                    try {
                        launchRoom.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有外卖没？[{}]", hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没外卖干不了");
                }
            } finally {
                lock.unlock();
            }
        }, "小女");
        t2.start();


        //送外卖的
        Thread t3 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                lock.lock();
                hasTakeout = true;
                log.debug("外卖到了");
                launchRoom.signalAll();
            } finally {
                lock.unlock();
            }
        }, "送外卖的");
        t3.start();

        t1.join();
        t2.join();
        t3.join();
    }
}
