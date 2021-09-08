package com.concurrent.p4;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j(topic = "c.TestQuestionPhilosopher2")
public class TestQuestionPhilosopher2 {
    @Test
    public void test1() {
        Chopstick2 c1 = new Chopstick2("1");
        Chopstick2 c2 = new Chopstick2("2");
        Chopstick2 c3 = new Chopstick2("3");
        Chopstick2 c4 = new Chopstick2("4");
        Chopstick2 c5 = new Chopstick2("5");
        new Philosopher2("苏格拉底", c1, c2).start();
        new Philosopher2("柏拉图", c2, c3).start();
        new Philosopher2("亚里士多德", c3, c4).start();
        new Philosopher2("赫拉克利特", c4, c5).start();
        new Philosopher2("阿基米德", c5, c1).start();
        while (true) ;
    }
}

@Slf4j(topic = "c.Philosopher2")
class Philosopher2 extends Thread {
    private String name;
    private Chopstick2 left;
    private Chopstick2 right;

    public Philosopher2(String name, Chopstick2 left, Chopstick2 right) {
        super(name);
        this.left = left;
        this.right = right;
    }

    public void eat() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("eat...");
    }

    @Override
    public void run() {
//        while (true) {
//            try {
//                left.tryLock(1, TimeUnit.SECONDS);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                return;
//            }
//            try {
//                right.tryLock(1, TimeUnit.SECONDS);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                return;
//            }
//            try {
//                eat();
//            } finally {
//                left.unlock();
//                right.unlock();
//            }
//        }
        while (true) {
            if (left.tryLock()) {      //尝试获取左筷子
                try {
                    if (right.tryLock()) {      //尝试获取右筷子
                        try {
                            eat();  //都获取到开始eat
                        } finally {
                            right.unlock();
                        }
                    }
                } finally {
                    left.unlock();  //***没有获取到right，则会释放left
                }
            }
        }
    }
}

class Chopstick2 extends ReentrantLock {
    private String name;

    public Chopstick2(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Chopstick2{" +
                "name='" + name + '\'' +
                '}';
    }
}