package com.concurrent.p3;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

@Slf4j(topic = "c.TestPark_Unpark")
public class TestPark_Unpark {

    /**
     * 暂停当前线程
     * LockSupport.park();
     * 恢复某个线程的运行
     * LockSupport.unpark(暂停线程对象)
     */
    @Test
    public void testPark_Unpark() {
        //线程1
        Thread t1 = new Thread(() -> {
            log.debug("start...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("park...");
            //park
            LockSupport.park();
            log.debug("resume...");
        }, "t1");
        t1.start();

        //主线程中unpark
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("umpark...");
        LockSupport.unpark(t1);

        //等待t1执行完毕
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
