package com.concurrent.p2;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Hashtable;

@Slf4j(topic = "c.TestThreadSafeClass")
public class TestThreadSafeClass {
    /**
     * 多线程调用同一个实例的某个方法时，是线程安全的
     * 常见线程安全类：
     * String
     * Integer等包装类
     * StringBuffer
     * Random
     * Vector
     * HashTable
     * java.util.concurrent包下的类
     */
    @Test
    public void testHashTableSafe() throws InterruptedException {
        //创建HashTable对象
        Hashtable hashtable = new Hashtable();
        //线程1
        new Thread(() -> {
            hashtable.put("key1", "value1");
        }).start();
        //线程2
        new Thread(() -> {
            hashtable.put("key2", "value2");
        }).start();

        Thread.sleep(1000);
        //09:42:05.543 [main] DEBUG c.TestThreadSafeClass - {key2=value2, key1=value1}
        log.debug(hashtable.toString());

        /**
         * HashTable源码分析：
         * public synchronized V put(K key, V value){...}
         * put方法是加了同步锁的，锁是this对象。
         */
    }

    /**
     * 每一个是线程安全的，但是组合到一起可能就不是线程安全的
     */
    //定义一个组合了HashTable的get、put的方法，这两个方法本身是同步的
    //有可能执行多次put
    static void func(Hashtable hs) {
        if (hs.get("key_t") == null) {
            hs.put("key_t", "val_t");
        }
    }

    @Test
    public void testHashTableUnsafe() throws InterruptedException {
        Hashtable hs = new Hashtable();
        new Thread(() -> {
            func(hs);
        }).start();
        new Thread(() -> {
            func(hs);
        }).start();
        Thread.sleep(2000);
        log.debug(hs.toString());

    }


    /**
     * String Integer都是不可变类，内部的状态不可改变，因此他们的方法都是线程安全的。
     *
     */
    /**
     * String的replace方法
     */
    @Test
    public void testReplace() {
        String s = "abcdaaa";
        //将a字符替换为e字符
        s = s.replace("a", "e");
        log.debug("s:{}", s);   //s:ebcdeee

        /**
         * replace()分析:
         *  创建新的字符串对象，没有改变原来的字符串
         */
    }


}
