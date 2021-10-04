package com.concurrent.p10;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.locks.StampedLock;

@Slf4j(topic = "c.Test_StampedLock")
public class Test_StampedLock {

    /**
     * 测试读-读
     * <p>
     * 12:01:54.621 [Thread-0] DEBUG c.DataContainerStamped - 乐观读,stamped=256
     * 12:01:54.621 [Thread-1] DEBUG c.DataContainerStamped - 乐观读,stamped=256
     * 12:01:54.629 [Thread-1] DEBUG c.Test_StampedLock - 输出:abc
     * 12:01:54.629 [Thread-0] DEBUG c.Test_StampedLock - 输出:abc
     */
    @Test
    public void test1() {
        DataContainerStamped dataContainer = new DataContainerStamped("abc");
        Thread t1 = new Thread(() -> {
            String data = dataContainer.read();
            log.debug("输出:{}", data);
        });
        Thread t2 = new Thread(() -> {
            String data = dataContainer.read();
            log.debug("输出:{}", data);
        });
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试读-写
     *
     * (1)两个读线程先执行
     * 12:08:51.140 [Thread-0] DEBUG c.DataContainerStamped - 乐观读,stamped=256
     * 12:08:51.143 [Thread-1] DEBUG c.DataContainerStamped - 乐观读,stamped=256
     * 12:08:51.150 [Thread-0] DEBUG c.Test_StampedLock - 输出:abc
     * 12:08:51.150 [Thread-1] DEBUG c.Test_StampedLock - 输出:abc
     * 12:08:51.243 [Thread-2] DEBUG c.DataContainerStamped - 获取写锁,stamped=384
     * 12:08:51.243 [Thread-2] DEBUG c.DataContainerStamped - 开始写...
     * 12:08:53.244 [Thread-2] DEBUG c.DataContainerStamped - 写完成...
     * 12:08:53.244 [Thread-2] DEBUG c.DataContainerStamped - 释放写锁,stamped=384
     *
     * (2)写线程先执行
     * 12:09:45.258 [Thread-0] DEBUG c.DataContainerStamped - 乐观读,stamped=256
     * 12:09:45.263 [Thread-2] DEBUG c.DataContainerStamped - 获取写锁,stamped=384
     * 12:09:45.258 [Thread-1] DEBUG c.DataContainerStamped - 乐观读,stamped=256
     * 12:09:45.264 [Thread-2] DEBUG c.DataContainerStamped - 开始写...
     * 12:09:47.265 [Thread-0] DEBUG c.DataContainerStamped - 乐观读升级读锁,stamped=514
     * 12:09:47.265 [Thread-0] DEBUG c.DataContainerStamped - 开始读...,stamped=514
     * 12:09:47.265 [Thread-1] DEBUG c.DataContainerStamped - 乐观读升级读锁,stamped=513
     * 12:09:47.265 [Thread-1] DEBUG c.DataContainerStamped - 开始读...,stamped=513
     * 12:09:47.265 [Thread-2] DEBUG c.DataContainerStamped - 写完成...
     * 12:09:47.265 [Thread-2] DEBUG c.DataContainerStamped - 释放写锁,stamped=384
     * 12:09:48.265 [Thread-1] DEBUG c.DataContainerStamped - 结束读...,stamped=513
     * 12:09:48.265 [Thread-0] DEBUG c.DataContainerStamped - 结束读...,stamped=514
     * 12:09:48.265 [Thread-1] DEBUG c.DataContainerStamped - 释放读锁,stamped=513
     * 12:09:48.265 [Thread-0] DEBUG c.DataContainerStamped - 释放读锁,stamped=514
     * 12:09:48.265 [Thread-0] DEBUG c.Test_StampedLock - 输出:jkl
     * 12:09:48.265 [Thread-1] DEBUG c.Test_StampedLock - 输出:jkl
     */
    @Test
    public void test2() {
        DataContainerStamped dataContainer = new DataContainerStamped("abc");
        Thread t1 = new Thread(() -> {
            String data = dataContainer.read();
            log.debug("输出:{}", data);
        });
        Thread t2 = new Thread(() -> {
            String data = dataContainer.read();
            log.debug("输出:{}", data);
        });
        Thread t3 = new Thread(() -> {
            dataContainer.write("jkl");
        });

        t1.start();
        t2.start();
        t3.start();
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

@Slf4j(topic = "c.DataContainerStamped")
class DataContainerStamped {
    //共享数据
    private String data;
    //StampedLock
    private StampedLock lock = new StampedLock();

    public DataContainerStamped(String data) {
        this.data = data;
    }

    //写
    public void write(String data) {
        //获取写锁,并返回戳
        long stamped = lock.writeLock();
        log.debug("获取写锁,stamped={}", stamped);
        try {
            //睡2秒,模拟写入
            log.debug("开始写...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.data = data;
        } finally {
            //释放写锁,并设置戳
            lock.unlockWrite(stamped);
            log.debug("写完成...");
            log.debug("释放写锁,stamped={}", stamped);
        }
    }

    //读
    public String read() {
        //乐观读(此过程没有锁),返回戳
        long stamped = lock.tryOptimisticRead();
        log.debug("乐观读,stamped={}", stamped);
        //校验戳
        if (lock.validate(stamped)) {  //成功则返回结果
            return this.data;
        } else { //失败则将乐观读升级成读锁
            stamped = lock.readLock();
            log.debug("乐观读升级读锁,stamped={}", stamped);
            try {
                log.debug("开始读...,stamped={}", stamped);
                //模拟读取1秒
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("结束读...,stamped={}", stamped);
                return this.data;
            } finally {
                lock.unlockRead(stamped);
                log.debug("释放读锁,stamped={}", stamped);
            }
        }
    }


}