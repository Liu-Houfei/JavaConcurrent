package com.concurrent.p1;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j(topic = "c.TestMainDaemonThread")
public class TestMainDaemonThread {

    /**
     * 默认情况下，Java 进程需要等待所有线程都运行结束，才会结束。
     * <p>
     * 有一种特殊的线程叫做守护线程，只要其它非守护线程运行结束了，
     * 即使守护线程的代码没有执行完，也会强制结束。
     */
    @Test
    public void t1() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
        });
        t1.start();
        Thread.sleep(1000);
        log.debug("结束");
    }

    @Test
    public void t2() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            }
        });
        //在启动前，将t1设为守护线程
        t1.setDaemon(true);
        t1.start();
        Thread.sleep(1000);
        log.debug("结束");
    }

}
