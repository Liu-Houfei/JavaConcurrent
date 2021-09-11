package com.concurrent.p6;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 可见性
 */
@Slf4j(topic = "c.TestVisiblity")
public class TestVisiblity {

    //共享变量可见性问题
    //static boolean run = true;
    //添加volatile后，线程会一直在主存中读取值，不再使用缓存。
    volatile static boolean run = true;

    @Test
    public void test1() {
        Thread t1 = new Thread(() -> {
            while (run) {
                //...
            }
        }, "t1");
        t1.start();

        try {
            Thread.sleep(1000);
            run = false;    //t1线程不会停止
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //使用synchronized解决可见性问题
    static boolean flag = true;
    static final Object lock = new Object();

    @Test
    public void test2() {
        Thread t1 = new Thread(() -> {
            while (run) {
                synchronized (lock) {
                    //...
                    if (!run) {
                        break;
                    }
                }
            }
        }, "t1");
        t1.start();
        try {
            Thread.sleep(1000);
            run = false;    //t1线程不会停止
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //在while块中添加  System.out.println();
    static boolean flag3 = true;

    @Test
    public void test3() {
        Thread t1 = new Thread(() -> {
            while (flag3) {
                //...
                System.out.println("sout");
            }
        }, "t1");
        t1.start();

        try {
            Thread.sleep(1000);
            flag3 = false;    //t1线程不会停止
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
