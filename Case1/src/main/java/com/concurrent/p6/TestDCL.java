package com.concurrent.p6;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 测试DCL(double-checked locking)
 */
@Slf4j(topic = "c.TestDCL")
public class TestDCL {

    static Singleton singleton;

    @Test
    public void testDCL() {
        for (int i = 0; i < 200; i++) {
            new Thread(() -> {
                singleton = Singleton.getInstance();
            }, String.valueOf(i)).start();
        }

        while (singleton != null) {
            Thread.yield();
        }
    }
}

/**
 * 单例类
 */
@Slf4j(topic = "c.Singleton")
class Singleton {
    //要添加volatile，防止指令重排。
    // INSTANCE = new Singleton();多线程下，可能先赋值，再调用构造方法。
    // 其他线程可能会获得空对象。
    private volatile static Singleton INSTANCE = null;

    private Singleton() {
        log.debug("创建单例对象");
    }

    //DCL
    public static Singleton getInstance() {
        //先做1次判断
        if (INSTANCE != null) {
            return INSTANCE;
        }
        //多线程情况下，要对创建实例对象加锁，
        // sync中还加一次判断，防止阻塞队列中的线程再创建新实例
        synchronized (Singleton.class) {
            //如果不是第一次创建
            if (INSTANCE != null) {
                return INSTANCE;
            }
            //如果是第一次创建
            INSTANCE = new Singleton();
            return INSTANCE;
        }
    }

}