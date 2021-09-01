package com.concurrent.p1;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j(topic = "c.TestSleepYield")
public class TestSleepYield {

    /**
     * 测试线程t1调用sleep之后的线程状态
     * sleep:RUNNABLE-->TIMED_WAITING
     */
    @Test
    public void t1() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("running...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1");
        log.debug("t1 state:{}", t1.getState());    //10:15:25.443 [main] DEBUG c.TestSleepYield - t1 state:NEW
        t1.start();
        log.debug("t1 state:{}", t1.getState());    //10:15:25.448 [main] DEBUG c.TestSleepYield - t1 state:RUNNABLE
        Thread.sleep(1000);
        log.debug("t1 state:{}", t1.getState());    //10:22:50.018 [main] DEBUG c.TestSleepYield - t1 state:TIMED_WAITING
    }

    /**
     * interrupt()方法打断正在sleep的线程（唤醒线程）
     */
    @Test
    public void t2() throws InterruptedException {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                log.debug("enter sleep...");
                //睡眠3秒
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    log.debug("wake up...");    //异常唤醒
                    e.printStackTrace();
                }
                log.debug("end sleep...");
            }
        };
        log.debug("t1 state:{}", t1.getState());    //[main] DEBUG c.TestSleepYield - t1 state:NEW
        t1.start();
        log.debug("t1 state:{}", t1.getState());    //[main] DEBUG c.TestSleepYield - t1 state:RUNNABLE
        //主线程睡眠1秒
        Thread.sleep(100);
        log.debug("interrupt...");
        t1.interrupt();
        log.debug("t1 state:{}", t1.getState());    //12:45:13.323 [main] DEBUG c.TestSleepYield - t1 state:RUNNABLE
    }

}
