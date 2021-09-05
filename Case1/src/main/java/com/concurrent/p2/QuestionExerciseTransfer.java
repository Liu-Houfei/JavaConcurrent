package com.concurrent.p2;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * 转账练习
 */
@Slf4j(topic = "c.Account")
class Account {
    //共享变量，存在多线程安全问题
    private int money;

    //定义锁
    private static final Object lock = new Object();

    //构造方法
    public Account(int money) {
        this.money = money;
    }

    public int getMoney() {
        synchronized (lock) {
            return money;
        }
    }

    public void setMoney(int money) {
        synchronized (lock) {
            this.money = money;
        }
    }

    //转账方法
    //有两个需要保护的共享变量
    //可以锁住类本身Account.class 或者 定义一个锁
    public void transfer(Account target, int amount) {
        synchronized (lock) {
            if (this.money >= amount) {
                this.setMoney(this.getMoney() - amount);    //临界区
                target.setMoney(target.getMoney() + amount);    //临界区
            }
        }
    }
}

@Slf4j(topic = "c.QuestionExerciseTransfer")
public class QuestionExerciseTransfer {

    static Random random = new Random();

    public static int randomAmount() {
        return random.nextInt(5) + 1;
    }

    public static void main(String[] args) {
        //创建两个账户
        Account a = new Account(2000);
        Account b = new Account(1000);
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                //在线程1中转账1000次
                for (int i = 0; i < 1000; i++) {
                    a.transfer(b, randomAmount());  //临界区
                }
            }
        };
        t1.start();
        Thread t2 = new Thread("t2") {
            @Override
            public void run() {
                //在线程1中转账1000次
                for (int i = 0; i < 1000; i++) {
                    b.transfer(a, randomAmount());  //临界区
                }
            }
        };
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //输出a,b的钱
        log.debug("a:{}，b:{},sum:{}", a.getMoney(), b.getMoney(), a.getMoney() + b.getMoney());

        /**
         * 转账时不加同步锁，线程不安全
         * 10:08:04.968 [main] DEBUG c.QuestionExerciseTransfer - a:1721，b:1277,sum:2998
         *
         * 加了同步锁之后， private static final Object lock = new Object();
         * 不要直接在方法上加synchronized，会造成死锁！
         *
         * 10:15:52.357 [main] DEBUG c.QuestionExerciseTransfer - a:989，b:2011,sum:3000
         */

    }


}