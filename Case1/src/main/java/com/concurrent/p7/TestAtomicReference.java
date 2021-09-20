package com.concurrent.p7;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 原子引用
 */
@Slf4j(topic = "c.TestAtomicReference")
public class TestAtomicReference {

    @Test
    public void test_AtomicReference_BigDecimal() {
        //创建账户对象
        DecimalAccountCAS account = new DecimalAccountCAS(new BigDecimal("1000000"));
        //100个线程取款,每个线程取10000
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                account.withdraw(new BigDecimal("10000"));
            }).start();
        }
        //主线程睡等待取款操作完成
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //输出账户余额
        log.debug("账户余额：{}", account.getAccount());

    }
}

interface DecimalAccount {
    //获取余额
    BigDecimal getAccount();

    //取款
    void withdraw(BigDecimal balance);
}

class DecimalAccountCAS implements DecimalAccount {

    //原子引用变量
    private AtomicReference<BigDecimal> balance;

    public DecimalAccountCAS(BigDecimal balance) {
        this.balance = new AtomicReference<>(balance);
    }

    @Override
    public BigDecimal getAccount() {
        return balance.get();
    }

    @Override
    public void withdraw(BigDecimal balance) {
        //CAS实现取款
        BigDecimal prev, next;
        do {
            //获取当前值
            prev = this.balance.get();
            //修改值
            next = prev.subtract(balance);
        } while (!this.balance.compareAndSet(prev, next));
    }
}