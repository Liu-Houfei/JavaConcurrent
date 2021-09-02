package com.concurrent.p2;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j(topic = "c.TestSynchronized2")
public class TestSynchronized2 {

    @Test
    public void testSynchronized() {
        Room room = new Room();
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    room.increment();
                }
            }
        };
        t1.start();
        Thread t2 = new Thread("t2") {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    room.decrement();
                }
            }
        };
        t2.start();
        //16:18:25.107 [main] DEBUG c.TestSynchronized2 - counter:0
        log.debug("counter:{}", room.getCounter());
    }
}

/**
 * 面向对象的改进：
 * 对共享资源的保护由内部来实现
 */
class Room {
    private int counter = 0;

    //加
    public void increment() {
        synchronized (this) {   //使用Room对象本身作为对象锁
            counter++;
        }
    }

    //减
    public void decrement() {
        synchronized (this) {   //使用Room对象本身作为对象锁
            counter--;
        }
    }

    //获取结果也要加锁
    public int getCounter() {
        synchronized (this) {
            return counter;
        }
    }

    //也可以把synchronized加在成员方法上，实际上还是锁住this对象
    synchronized public int getCounter_(){
        return counter;
    }
}
