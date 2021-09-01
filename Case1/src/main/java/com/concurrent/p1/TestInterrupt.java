package com.concurrent.p1;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.locks.LockSupport;

@Slf4j(topic = "c.TestInterrupt")
public class TestInterrupt {

    /**
     * 打断sleep的阻塞状态线程，会清空打断状态
     */
    @Test
    public void t1() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            //睡眠1秒
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.debug("线程被打断！");
                e.printStackTrace();
            }
        }, "t1");
        t1.start();
        Thread.sleep(1000);
        log.debug("打断前标记：{}", t1.isInterrupted());   //false
        t1.interrupt(); //打断t1线程
        /**
         * sleep,wait,join 以异常的方式被打断，打断标记被还原成false。
         * 如果是正常运行的线程被打断，则打断标记为true
         */
        log.debug("打断后标记：{}", t1.isInterrupted());   //false
    }

    /**
     * 打断正常运行的线程
     */
    @Test
    public void t2() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while (true) {
                boolean isInterrupted = Thread.currentThread().isInterrupted();
                log.debug("打断标记状态：{}", isInterrupted);
                if (isInterrupted) {
                    log.debug("被打断，退出循环");
                    break;
                }
            }
        }, "t1");
        t1.start();
        Thread.sleep(10);
        log.debug("打断前标记：{}", t1.isInterrupted());  //false
        t1.interrupt();
        log.debug("打断后标记：{}", t1.isInterrupted());  //true
    }

    /**
     * 模式：两阶段终止
     * 在一个线程t1中如何“优雅”终止线程t2
     * <p>
     * 错误思路：
     * 1、使用线程对象的stop()方法停止线程：stop方法会真正杀死线程，如果这时线程锁住了共享资源，
     * 那么当它被杀死后就再也没有机会释放锁，其他线程将永远不能获得锁。
     * <p>
     * 2、使用System.exit(int)方法停止线程：这会让整个程序终止。
     */
    @Test
    public void t3() throws InterruptedException {
        TwoPhaseTermination termination = new TwoPhaseTermination();
        //开始监控
        termination.start();
        //5秒后停止监控
        Thread.sleep(5000);
        termination.stop();

    }

    @Slf4j(topic = "c.TwoPhaseTermination")
    static class TwoPhaseTermination {
        private Thread monitor;

        //开始监控
        public void start() {
            monitor = new Thread(() -> {
                //监控逻辑
                while (true) {
                    //获取打断标记
                    boolean isInterrupted = Thread.currentThread().isInterrupted();
                    // 如果是正常打断，isInterrupted=true，则料理后事并退出循环
                    if (isInterrupted) {
                        log.debug("料理后事");
                        break;
                    }
                    log.debug("监控系统...");
                    try {
                        //设置监控间隔时间
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        //在阻塞状态下被打断，会跑出异常，并且打断状态被还原成false
                        //小技巧：在异常中，在执行1次打断操作，即可将打断标记设为true
                        monitor.interrupt();
                        e.printStackTrace();
                    }
                }
            });
            monitor.start();
        }

        //停止监控
        public void stop() {
            monitor.interrupt();
        }
    }


    /**
     * interrupt打断park线程
     */
    @Test
    public void t4() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("park...");
            LockSupport.park();
            log.debug("unpark...");
            log.debug("打断标记：{}", Thread.currentThread().isInterrupted());
        });
        t1.start();
        Thread.sleep(3000);
        t1.interrupt();
    }

    /**
     * interrupt()打断park线程后，打断标记为true，再次调用park()不会生效
     */
    @Test
    public void t5() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("park...");
            LockSupport.park();
            log.debug("unpark...");
            log.debug("打断标记：{}", Thread.currentThread().isInterrupted());
            LockSupport.park();     //失效
            log.debug("unpark...");
            log.debug("打断标记：{}", Thread.currentThread().isInterrupted());
        });
        t1.start();
        Thread.sleep(3000);
        t1.interrupt();
        Thread.sleep(3000);
        t1.interrupt();
    }

    /**
     * interrupted()打断park()，再低park()时不会失效。
     */
    @Test
    public void t6() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            log.debug("park...");
            LockSupport.park();
            log.debug("unpark...");
            //使用Thread.interrupted();重置打断标记
            log.debug("打断标记：{}",  Thread.interrupted());
            log.debug("park...");
            LockSupport.park();     //不会失效
            log.debug("unpark...");
        });
        t1.start();
        Thread.sleep(3000);
        t1.interrupt();
        Thread.sleep(5000);
        t1.interrupt();
    }
}
