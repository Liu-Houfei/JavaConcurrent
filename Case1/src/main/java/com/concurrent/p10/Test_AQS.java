package com.concurrent.p10;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 测试自定义不可重入锁
 */
@Slf4j(topic = "c.Test_AQS")
public class Test_AQS {

    static int i = 0;

    @Test
    public void test_MyAQS() {
        MyLock myLock = new MyLock();
        List<Thread> threadList = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            threadList.add(new Thread(() -> {
                myLock.lock();
                log.debug("获得锁...");
                try {
                    for (int k = 0; k < 10000; k++) {
                        i++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    myLock.unlock();
                    log.debug("释放锁...");
                }
            }));
        }
        for (int j = 0; j < 5; j++) {
            threadList.add(new Thread(() -> {
                myLock.lock();
                log.debug("获得锁...");
                try {
                    for (int k = 0; k < 10000; k++) {
                        i--;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    myLock.unlock();
                    log.debug("释放锁...");

                }
            }));
        }
        threadList.forEach((s) -> {
            s.start();
        });
        threadList.forEach((s) -> {
            try {
                s.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        log.debug("i={}", i);
    }

}

/**
 * 自定义不可重入锁
 */
class MyLock implements Lock {

    /**
     * 自定义AQS同步器
     */
    static class MySync extends AbstractQueuedSynchronizer {
        //获得锁
        @Override
        protected boolean tryAcquire(int arg) {
            if (arg == 1) {
                //此处不能用1次if判断,要用do...while做CAS操作
//                if (compareAndSetState(0, 1)) {
//                    setExclusiveOwnerThread(Thread.currentThread());
//                    setState(1);
//                    return true;
//                }
                do{

                }while(!compareAndSetState(0, 1));
                setExclusiveOwnerThread(Thread.currentThread());
                setState(1);
                return true;
            }
            return false;
        }

        //释放锁
        @Override
        protected boolean tryRelease(int arg) {
            if (arg == 1) {
                if (getState() == 0) {
                    throw new IllegalMonitorStateException();
                }
                setExclusiveOwnerThread(null);
                setState(0);
                return true;
            }
            return false;
        }

        //判断是否持有独占锁
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        //获取条件变量
        protected Condition newCondition() {
            return new ConditionObject();
        }
    }

    private static MySync mySync = new MySync();

    @Override
    public void lock() {
        mySync.tryAcquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        mySync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return mySync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return mySync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        mySync.release(1);
    }

    @Override
    public Condition newCondition() {
        return mySync.newCondition();
    }
}
