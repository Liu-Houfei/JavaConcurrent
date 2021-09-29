package com.concurrent.p10;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j(topic = "c.Test_ReentrantReanWriteLock")
public class Test_ReentrantReanWriteLock {

    class DataContainer {
        private Object data;
        //读写锁
        private ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        //读锁
        private ReentrantReadWriteLock.ReadLock r = rw.readLock();
        //写锁
        private ReentrantReadWriteLock.WriteLock w = rw.writeLock();

        public DataContainer(Object data) {
            this.data = data;
        }

        //读
        public Object read() {
            log.debug("获取读锁...");
            r.lock();
            try {
                log.debug("开始读...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return data;
            } finally {
                log.debug("释放读锁...");
                r.unlock();
            }
        }

        //写
        public void write(Object obj) {
            log.debug("获取写锁...");
            w.lock();
            try {
                log.debug("开始写...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                data = obj;
            } finally {
                log.debug("释放写锁...");
                w.unlock();
            }
        }
    }

    @Test
    public void test_ReentrantReadWriteLock() {
        DataContainer dataContainer = new DataContainer("test");
        List<Thread> readThreads = new ArrayList<>();
        List<Thread> writeThreads = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            readThreads.add(
                    new Thread(() -> {
                        Object o = dataContainer.read();
                        log.debug(o.toString());
                    })
            );
        }
        for (int i = 0; i < 2; i++) {
            writeThreads.add(
                    new Thread(() -> {
                        dataContainer.write("random:" + new Random().nextInt(100));
                    })
            );
        }
        readThreads.forEach((s) -> {
            s.start();
        });
        writeThreads.forEach((s) -> {
            s.start();
        });
        readThreads.forEach((s) -> {
            try {
                s.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        writeThreads.forEach((s) -> {
            try {
                s.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
