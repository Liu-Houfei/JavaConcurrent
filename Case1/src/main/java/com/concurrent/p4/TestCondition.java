package com.concurrent.p4;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 条件变量
 */
@Slf4j(topic = "c.TestCondition")
public class TestCondition {

    static ReentrantLock lock = new ReentrantLock();

    @Test
    public void test1() {
        //创建新的条件变量（休息室）
        Condition condition1 = lock.newCondition();
        Condition condition2 = lock.newCondition();

        Thread t1 = new Thread(() -> {
            try {
                lock.lock();
                try {
                    log.debug("开始等待");
                    //进入休息室等待
                    condition1.await();
                    log.debug("结束等待");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } finally {
                lock.unlock();
            }

        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            //t2线程5秒后唤醒休息室中的线程t1
            try {
                lock.lock();
                try {
                    log.debug("休眠3秒...");
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("唤醒休息室1中的线程");
                condition1.signal();
            } finally {
                lock.unlock();
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
