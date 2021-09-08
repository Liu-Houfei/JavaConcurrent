package com.concurrent.p3.message_queue;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "c.TestMessageQueue")
public class TestMessageQueue {

    /**
     * 1个生产者，1个消费者
     */
    @Test
    public void testMessageQueue1() {
        MessageQueue mq = new MessageQueue(5);
        //生产者线程
        Thread procedure = new Thread(() -> {
            while (true) {
                mq.complete();
            }
        }, "procedure");
        procedure.start();

        //消费者线程
        Thread customer = new Thread(() -> {
            while (true) {
                //每个1秒，消费1次
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = mq.get();
                log.debug(message.toString());
            }
        }, "customer");
        customer.start();

        //在主线程查看队列的情况
        while (true) {
            //每个2秒查看消息队列的情况
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mq.showList();
        }
    }

    /**
     * 3个生产者，1个消费者
     */
    @Test
    public void testMessageQueue2() {
        //3个生产者线程
        List<Thread> procedureThreadList = new ArrayList<>();
        //消息队列
        MessageQueue mq = new MessageQueue(10);
        for (int i = 0; i < 3; i++) {
            Thread p = new Thread(() -> {
                while (true) {
                    mq.complete();
                }
            }, "p" + i + 1);
            procedureThreadList.add(p);
        }
        for (Thread i : procedureThreadList) {
            i.start();
        }

        //1个消费者
        Thread c1 = new Thread(()->{
            while (true) {
                Message message = mq.get();
                //log.debug(message.toString());
            }
        },"c1");
        c1.start();

        //在主线程查看队列的情况
        while (true) {
            //每个2秒查看消息队列的情况
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mq.showList();
        }
    }
}
