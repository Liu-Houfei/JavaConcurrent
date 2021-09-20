package com.concurrent.p7;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * ABA问题
 */
@Slf4j(topic = "c.TestABAQuestion")
public class TestABAQuestion {

    /**
     * ABA问题复现
     */
    @Test
    public void test_ABAQuestion() {
        AtomicInteger ai = new AtomicInteger(10);
        //线程1睡眠1秒后修改ai变量，10->20->10
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ai.compareAndSet(10, 20);
            ai.compareAndSet(20, 10);
        }, "t1");
        t1.start();

        //线程2先获取ai的值，3秒后再获取ai的值
        Thread t2 = new Thread(() -> {
            int prev = ai.get();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //虽然t1线程修改过ai的值，但是结果仍是true
            System.out.println(ai.compareAndSet(prev, 20));  //true
        }, "t2");
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * ABA问题解决
     * 使用 AtomicStampedReference ，带有版本号（时间戳）的原子引用
     */
    @Test
    public void test_ABA_AtomicStampedReference() {
        AtomicStampedReference<Integer> ai =
                new AtomicStampedReference<>(10, 1);
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /**
             * @param expectedReference the expected value of the reference  期望值
             * @param newReference the new value for the reference   新值
             * @param expectedStamp the expected value of the stamp  期望时间戳
             * @param newStamp the new value for the stamp  新时间戳
             */
            //获取当前时间戳
            ai.compareAndSet(10, 20, ai.getStamp(), ai.getStamp() + 1);
            ai.compareAndSet(20, 10, ai.getStamp(), ai.getStamp() + 1);
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            //获取时间戳
            int stamp = ai.getStamp();
            //获取快照
            int prev = ai.getReference();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(ai.compareAndSet(prev, 20, stamp, stamp + 1));  //false
        }, "t2");
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 不关心变量更改了几次，只关心变量是否更改过，用AtomicMarkableReference
     */
    @Test
    public void test_ABA_AtomicMarkableReference() {
        //
        AtomicMarkableReference<Integer> amr =
                new AtomicMarkableReference<>(10, false);
        //线程1
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /*
             * @param expectedReference the expected value of the reference  期望值
             * @param newReference the new value for the reference  新值
             * @param expectedMark the expected value of the mark   期望标记
             * @param newMark the new value for the mark  新标记
             */
            amr.compareAndSet(10, 20, false, true);
        }, "t1");
        t1.start();
        //线程2
        Thread t2 = new Thread(() -> {
            Integer prev = amr.getReference();  //获取快照
            boolean mark = amr.isMarked();  //获取是否改变
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //false
            System.out.println(amr.compareAndSet(20, 10, false, true));
        }, "t2");
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
