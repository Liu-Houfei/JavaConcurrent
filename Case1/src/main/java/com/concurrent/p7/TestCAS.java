package com.concurrent.p7;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * CAS测试
 */
@Slf4j(topic = "c.TestCAS")
public class TestCAS {

    @Test
    public void testCAS() {
        Account account = new Account(100000);
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                account.cost(100);
            }).start();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("account:" + account.getAccount());
    }
}

class Account {
    //private double account;
    //定义原子引用类
    private  AtomicInteger balance;

    public Account(int balance) {
        this.balance = new AtomicInteger(balance);
    }

    public int getAccount() {
        return balance.get();
    }

    /**
     * 存在线程安全问题：
     * （1）使用synchronized解决
     * （2）使用ReentrantLock解决、
     * （3）使用CAS+volatile解决(无锁)
     */
    public void cost(int x) {
        int t;
        do {
            t = balance.get();  //快照
        } while (!balance.compareAndSet(t, t - x));  //CAS
    }
}