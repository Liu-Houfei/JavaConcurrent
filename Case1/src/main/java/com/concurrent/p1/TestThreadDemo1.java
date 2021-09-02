package com.concurrent.p1;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 烧水泡茶问题
 */
@Slf4j(topic = "c.TestThreadDemo1")
public class TestThreadDemo1 {

    @Test
    public void test() {
        long start = System.currentTimeMillis();
        //线程1，洗水壶1分钟和烧开水15分钟
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                try {
                    log.debug("洗水壶，1分钟");
                    Thread.sleep(1000);
                    log.debug("烧开水，15分钟");
                    Thread.sleep(1000 * 15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t1.start();
        //线程2，洗茶壶、洗茶杯、拿茶叶4分钟
        Thread t2 = new Thread("t1") {
            @Override
            public void run() {
                try {
                    log.debug("洗茶壶,1分钟");
                    Thread.sleep(1000);
                    log.debug("洗茶杯，1分钟");
                    Thread.sleep(1000);
                    log.debug("拿茶叶，2分钟");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t2.start();
        //等待t1，t2完成
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("泡茶");
        long end = System.currentTimeMillis();
        log.debug("耗时：{}", end - start);
    }
    /**
     * 15:06:58.516 [t1] DEBUG c.TestDemo1 - 洗水壶，1分钟
     * 15:06:58.516 [t1] DEBUG c.TestDemo1 - 洗茶壶,1分钟
     * 15:06:59.520 [t1] DEBUG c.TestDemo1 - 烧开水，15分钟
     * 15:06:59.520 [t1] DEBUG c.TestDemo1 - 洗茶杯，1分钟
     * 15:07:00.521 [t1] DEBUG c.TestDemo1 - 拿茶叶，2分钟
     * 15:07:14.521 [main] DEBUG c.TestDemo1 - 泡茶
     * 15:07:14.521 [main] DEBUG c.TestDemo1 - 耗时：16017
     */
}
