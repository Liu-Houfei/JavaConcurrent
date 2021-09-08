package com.concurrent.p3.message_queue;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.Random;

@Slf4j(topic = "c.MessageQueue")
public class MessageQueue {
    //消息队列
    private LinkedList<Message> list = new LinkedList<>();
    //队列容量
    private Integer capacity;

    //构造方法，初始化容量
    public MessageQueue(Integer capacity) {
        this.capacity = capacity;
    }

    //获取消息
    public Message get() {
        synchronized (this) {
            //如果队列为空，消费者就一直等待
            while (list.size() == 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //如果队列不为空，则取出队头消息，并唤醒生产者
            Message message = list.removeFirst();
            this.notifyAll();
            return message;
        }
    }

    //产生消息
    public void complete() {
        synchronized (this) {
            //如果队列满，生产者就等待
            while (list.size() == capacity) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //如果队列不满，生产者就一直生产消息
            Message message = new Message("hello" + (new Random().nextInt(100) + 1));
            //将产生的消息放到队列尾
            list.addLast(message);
            //唤醒消费者
            this.notifyAll();
        }
    }

    //查看当前队列的情况
    public void showList() {
        synchronized (this) {
            list.forEach((s) -> {
                log.debug(s.getMessage());
            });
            log.debug("=================");
        }
    }
}
