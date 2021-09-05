package com.concurrent.p2;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 卖票练习
 * 模拟多人买票
 */
@Slf4j(topic = "c.QuestionExerciseSell")
public class QuestionExerciseSell {
    static Random random = new Random();

    public static int randomAmount() {
        return random.nextInt(5) + 1;
    }

    public static void main(String[] args) {
        //共享售票窗口
        TicketWindow tw = new TicketWindow(50000);
        //线程集合
        List<Thread> list = new ArrayList<>();
        //用来存储卖出去多少张票
        List<Integer> sellCount = new ArrayList<>();
        //模拟2000人买票
        for (int i = 0; i < 2000; i++) {
            Thread t = new Thread(() -> {
                //对同一个共享变量的操作组合需要考虑安全，不同共享对象不用考虑
                int count = tw.sell(randomAmount());    //存在安全问题，需要加锁
                sellCount.add(count);      //源文件已经加锁
            });
            list.add(t);
            t.start();
        }
        //等到所有线程结束
        list.forEach((t) -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //卖出去的票总和
        log.debug("sell count :{}", sellCount.stream().mapToInt(c -> c).sum());
        //剩余票数
        log.debug("remainder count:{}", tw.getCount());
    }

    /**
     * sell()方法未加synchronized：
     * 09:35:04.258 [main] DEBUG c.QuestionExerciseSell - sell count :5967
     * 09:35:04.262 [main] DEBUG c.QuestionExerciseSell - remainder count:44031
     *
     * sell()方法加了synchronized
     * 09:56:01.672 [main] DEBUG c.QuestionExerciseSell - sell count :5998
     * 09:56:01.680 [main] DEBUG c.QuestionExerciseSell - remainder count:44002
     */
}

class TicketWindow {
    private int count;

    public TicketWindow(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    //不加同步会造成线程安全问题
    //要加上synchronized
    synchronized public int sell(int amount) {
        if (this.count >= amount) {
            this.count -= amount;   //临界区
            return amount;
        } else {
            return 0;
        }
    }
}