package com.concurrent.p4;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock可重入锁
 */
@Slf4j(topic = "c.TestReentrantLock")
public class TestReentrantLock {


    /**
     * 可重入
     * lock()
     */
    static ReentrantLock lock = new ReentrantLock();

    @Test
    public void t1() {
        lock.lock();
        try {
            log.debug("进入main方法");
            m1();   //锁重入
        } finally {
            lock.unlock();
        }
    }

    public static void m1() {
        lock.lock();
        try {
            log.debug("进入m1方法");
            m2();  //锁重入
        } finally {
            lock.unlock();
        }
    }

    public static void m2() {
        lock.lock();
        try {
            log.debug("进入m2方法");
        } finally {
            lock.unlock();
        }
    }


    /**
     * 可打断
     * lockInterruptibly()
     */
    static ReentrantLock lock2 = new ReentrantLock();

    @Test
    public void t2() {
        Thread t1 = new Thread(() -> {
            //如果没有竞争，则使用次方法获得lock对象锁
            //如果有竞争，则进入阻塞队列，可以被其他线程的interrupt方法打断
            try {
                log.debug("尝试获得锁");
                lock2.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("没有获得锁，返回");
                return; //表示被打断，没有获得锁
            }
            try {
                log.debug("获取到锁");
            } finally {
                lock2.unlock();
            }
        }, "t1");
        //主线程加锁
        lock2.lock();
        t1.start();
        //主线程谁3秒后打断
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.interrupt();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 锁超时
     * trylock()
     */
    static ReentrantLock lock3 = new ReentrantLock();

    @Test
    public void t3() {
        Thread t1 = new Thread(() -> {
            //尝试获得锁
            if (!lock3.tryLock()) {  //如果没有获得锁
                log.debug("获取锁，立刻失败，返回");
                return;
            }
            try {
                log.debug("获得了锁");
            } finally {
                lock3.unlock();
            }
        }, "t1");
        //先让主线程获得锁
        lock3.lock();
        log.debug("获得了锁");
        //t1启动后不能获得锁
        t1.start();

        //主线程2秒后释放锁
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock3.unlock();
        }

        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *  tryLock(2, TimeUnit.SECONDS)
     */
    @Test
    public void t4(){
        Thread t1 = new Thread(() -> {
            //尝试获得锁
            try {
                if (!lock3.tryLock(1, TimeUnit.SECONDS)) {  //如果没有获得锁
                    log.debug("获取锁，立刻失败，返回");
                    return;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                log.debug("获得了锁");
            } finally {
                lock3.unlock();
            }
        }, "t1");
        //先让主线程获得锁
        lock3.lock();
        log.debug("获得了锁");
        //t1启动后不能获得锁
        t1.start();

        //主线程2秒后释放锁
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock3.unlock();
        }

        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
