package com.concurrent.p1;

import org.junit.Test;

public class TestDebugThread {

    /**
     * 在主线程中查看栈内存，栈帧
     */
    @Test
    public void t1() {
        method1(10);    //在主线程处加断点
    }

    /**
     * 在不同线程中查看栈内存，栈帧
     */
    @Test
    public void t2() {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                method1(20);       //在子线程处加断点
            }
        };
        t1.setName("t1");
        t1.start();
        method1(10);     //在主线程处加断点
    }

    private static void method1(int x) {
        int y = x + 1;
        Object m = method2();
    }

    private static Object method2() {
        Object n = new Object();
        return n;
    }
}
