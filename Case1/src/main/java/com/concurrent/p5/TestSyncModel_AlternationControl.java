package com.concurrent.p5;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 同步模式-交替输出
 */
@Slf4j(topic = "c.TestSyncModel_AlternationControl")
public class TestSyncModel_AlternationControl {
    /**
     * wait-notify版
     * <p>
     * 等待标记法
     * a   1
     * b   2
     * c   3
     */
    //定义锁对象
    static final Object lock1 = new Object();
    //等待标记，不适合用3个布尔值判断
    static int flag = 1;

    @Test
    public void test1() {
        Thread t1 = new Thread(() -> {
            synchronized (lock1) {
                while (true) {
                    while (flag != 1) {
                        try {
                            lock1.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    log.debug("a");
                    flag = 2;
                    lock1.notifyAll();
                }
            }
        }, "t1");
        t1.start();

        Thread t2 = new Thread(() -> {
            synchronized (lock1) {
                while (true) {
                    while (flag != 2) {
                        try {
                            lock1.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    log.debug("b");
                    flag = 3;
                    lock1.notifyAll();
                }
            }
        }, "t2");
        t2.start();

        Thread t3 = new Thread(() -> {
            synchronized (lock1) {
                while (true) {
                    while (flag != 3) {
                        try {
                            lock1.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    log.debug("c");
                    flag = 1;
                    lock1.notifyAll();
                }
            }
        }, "t3");
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 交替控制输出类
     */
    class WaitNotify {
        //循环次数
        private int loopNum;
        //等待标记
        private int wflag;

        public WaitNotify(int loopNum, int wflag) {
            this.loopNum = loopNum;
            this.wflag = wflag;
        }

        /**
         * @param str      输出字符串
         * @param waitFlag 等待标记
         * @param nextFlag 下一个标记
         */
        public void print(String str, int waitFlag, int nextFlag) {
            for (int i = 0; i < loopNum; i++) {
                synchronized (this) {
                    //如果当前标记与等待标记不一致，则等待
                    while (wflag != waitFlag) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.print(str);
                    wflag = nextFlag;
                    this.notifyAll();
                }
            }
        }
    }

    @Test
    public void test2() {
        WaitNotify waitNotify = new WaitNotify(5, 1);
        new Thread(() -> {
            waitNotify.print("a", 1, 2);
        }).start();
        new Thread(() -> {
            waitNotify.print("b", 2, 3);
        }).start();
        new Thread(() -> {
            waitNotify.print("c", 3, 1);
        }).start();

        while (true) ;
    }


    /**
     * ReentrantLock
     */
    class AwaitSignal extends ReentrantLock {
        private int loopNum;

        public AwaitSignal(int loopNum) {
            this.loopNum = loopNum;
        }

        public void print(String str, Condition current, Condition next) {
            for (int i = 0; i < loopNum; i++) {
                //开始时，进入各自的休息室等待
                try {
                    lock();
                    try {
                        current.await();
                        System.out.print(str);
                        next.signal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } finally {
                    unlock();
                }
            }
        }
    }


    @Test
    public void test3() {
        AwaitSignal awaitSignal = new AwaitSignal(5);
        Condition a = awaitSignal.newCondition();
        Condition b = awaitSignal.newCondition();
        Condition c = awaitSignal.newCondition();

        Thread t1 = new Thread(() -> {
            awaitSignal.print("a", a, b);
        });
        t1.start();
        new Thread(() -> {
            awaitSignal.print("b", b, c);
        }).start();
        new Thread(() -> {
            awaitSignal.print("c", c, a);
        }).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {
                awaitSignal.lock();
                a.signalAll();
            } finally {
                awaitSignal.unlock();
            }
        }).start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * park,unpark 实现
     */
    class ParkUnpark {
        private int loopNum;

        public ParkUnpark(int loopNum) {
            this.loopNum = loopNum;
        }

        public void print(String str, Thread nextThread) {
            for (int i = 0; i < loopNum; i++) {
                LockSupport.park();
                System.out.print(str);
                LockSupport.unpark(nextThread);
            }
        }
    }

    static Thread t1;
    static Thread t2;
    static Thread t3;

    @Test
    public void test4() {
        ParkUnpark parkUnpark = new ParkUnpark(5);
        t1 = new Thread(() -> {
            parkUnpark.print("a", t2);
        });
        t2 = new Thread(() -> {
            parkUnpark.print("b", t3);
        });
        t3 = new Thread(() -> {
            parkUnpark.print("c", t1);
        });
        t1.start();
        t2.start();
        t3.start();

        //发起线程
        new Thread(() -> {
            LockSupport.unpark(t1);
        }).start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
