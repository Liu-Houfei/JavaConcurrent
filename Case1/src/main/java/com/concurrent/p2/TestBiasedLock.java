package com.concurrent.p2;

/**
 * 偏向锁
 */
public class TestBiasedLock {
    static final Object obj = new Object();

    public static void f1() {

        synchronized (obj) {
            //同步块A
            f2();
        }
    }

    //锁重入
    public static void f2() {
        synchronized(obj){
            //同步块B
            f3();
        }
    }

    //锁重入
    public static void f3() {
        synchronized (obj){
            //同步块C
        }
    }

}
