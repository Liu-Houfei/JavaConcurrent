package com.concurrent.p1;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j(topic = "c.TestJoin")
public class TestJoin {

    /**
     * join()方法，等到线程执行结束后再往下执行
     */
    static int r = 0;

    @Test
    public void t1() throws InterruptedException {
        log.debug("开始");
        Thread t1 = new Thread(() -> {
            log.debug("开始");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("结束");
            r = 10;
        }, "t1");
        t1.start();
        t1.join();
        log.debug("r结果：{}", r);     //不加join()，r=0；加了join()，r=10
        log.debug("结束");
    }


    /**
     * join()耗费时间取决于耗时最长的线程
     */
    static int r1 = 0;
    static int r2 = 0;

    @Test
    public void t2() throws InterruptedException {
        //定义两个线程分别操作r1,r2
        Thread t1 = new Thread(() -> {
            log.debug("开始");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("结束");
            r1 = 20;
        }, "t1");
        t1.start();
        Thread t2 = new Thread(() -> {
            log.debug("开始");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("结束");
            r1 = 10;
        }, "t2");
        t2.start();

        long start = System.currentTimeMillis();
        t1.join();
        t2.join();
        long end = System.currentTimeMillis();
        //15:54:25.572 [main] DEBUG c.TestJoin - r1:20,r2:0,cost:2007
        //取决于耗时最长的线程
        log.debug("r1:{},r2:{},cost:{}", r1, r2, end - start);
    }

    /**
     * 有时效的join(time)：超时结束
     */
    static int m1 = 0;
    static int m2 = 0;

    @Test
    public void t3() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                m1++;
            }
        });

        Thread t2 = new Thread(() -> {
            while (true) {
                m2++;
            }
        });
        long start = System.currentTimeMillis();
        t1.start();
        t2.start();
        t1.join(100);
        t2.join(200);
        long end = System.currentTimeMillis();
        //16:02:06.444 [main] DEBUG c.TestJoin - m1:43392618,m2:43875752,cost:312
        log.debug("m1:{},m2:{},cost:{}", m1, m2, end - start);
    }
}
