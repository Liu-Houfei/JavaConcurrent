package com.concurrent.p4;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j(topic = "c.TestLiveLock")
public class TestLiveLock {
    static volatile int count = 10;
    static final Object lock = new Object();

    @Test
    public void t1() {
        //期望减到0退出循环
        new Thread(() -> {
            while (count > 0) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count--;
                log.debug("count={}", count);
            }
        }, "t1").start();
        //期望加到20退出循环
        new Thread(() -> {
            while (count < 20) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count++;
                log.debug("count={}", count);
            }
        }, "t2").start();

        while (true) ;
    }
}
