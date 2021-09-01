package com.concurrent.p1;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j(topic = "c.TestStartRun")
public class TestStartRun {

    @Test
    public void t1() {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                log.debug("running...");
            }
        };
        /**
         * 直接调用线程对象的run()方法，是在主线程（不是t1线程对象的线程栈）中执行的。
         * 在start()之后调用run()方法，是在新开的线程中执行的。
         */
        t1.run();   //10:01:57.345 [main] DEBUG c.TestStartRun - running...
        t1.start(); //10:01:57.355 [t1] DEBUG c.TestStartRun - running...
    }
}
