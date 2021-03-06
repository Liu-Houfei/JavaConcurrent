package com.concurrent.p2;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.text.NumberFormat;

/**
 * 线程8锁问题
 */
@Slf4j(topic = "c.QuestionSynchronized")
public class QuestionSynchronized {
    //情况1
    @Slf4j(topic = "c.Number1")
    static class Number1 {
        public synchronized void a() {
            log.debug("1");
        }

        public synchronized void b() {
            log.debug("2");
        }
    }

    //synchronized用在方法上，锁住的是this对象
    //打印1，再打印2
    //或者打印2，再打印1
    @Test
    public void t1() {
        Number1 n1 = new Number1();
        new Thread(() -> {
            n1.a();     //1
        }).start();
        new Thread(() -> {
            n1.b();     //2
        }).start();
        log.debug("end");
    }


    //情况2
    static class Number2 {
        public synchronized void a() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("1");
        }

        public synchronized void b() {
            log.debug("2");
        }
    }

    //休眠1秒后打印1，接着打印2
    //或者打印2，休眠1秒后打印1
    @Test
    public void t2() throws InterruptedException {
        Number2 n2 = new Number2();
        new Thread(() -> {
            n2.a();
        }).start();
        new Thread(() -> {
            n2.b();
        }).start();

        Thread.sleep(3000);
        log.debug("end");
    }

    //情况3
    static class Number3 {
        public synchronized void a() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("1");
        }

        public synchronized void b() {
            log.debug("2");
        }

        public void c() {
            log.debug("3");
        }
    }

    //1->2->3
    //1->3->2
    //2->1->3
    //2->3->1
    //3->1->2
    //3->2->1
    @Test
    public void t3() throws InterruptedException {
        Number3 n3 = new Number3();
        new Thread(() -> {
            n3.a();
        }).start();
        new Thread(() -> {
            n3.b();
        }).start();
        new Thread(() -> {
            n3.c();
        }).start();
        Thread.sleep(3000);
        log.debug("end");
    }

    //情况4
    static class Number4 {
        public synchronized void a() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("1");
        }

        public synchronized void b() {
            log.debug("2");
        }
    }

    //n1,n2不是同一个对象，不是同一个锁，不会同步
    //先2后1
    @Test
    public void t4() throws InterruptedException {
        Number4 n1 = new Number4();
        Number4 n2 = new Number4();
        new Thread(() -> {
            n1.a();
        }).start();
        new Thread(() -> {
            n2.b();
        }).start();
        Thread.sleep(3000);
        log.debug("end");
    }

    //情况5
    //先2后1
    static class Number5 {
        //static synchronized 静态同步方法的锁对象是类对象
        public static synchronized void a() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("1");
        }

        //锁住的是this对象
        public synchronized void b() {
            log.debug("2");
        }
    }

    @Test
    public void t5() throws InterruptedException {
        Number5 n = new Number5();
        new Thread(() -> {
            n.a();
        }).start();
        new Thread(() -> {
            n.b();
        }).start();
        Thread.sleep(3000);
        log.debug("end");
    }

    //情况6
    static class Number6 {
        //static synchronized 静态同步方法的锁对象是类对象
        public static synchronized void a() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("1");
        }

        //锁住的是类对象
        public static synchronized void b() {
            log.debug("2");
        }
    }

    @Test
    public void t6() throws InterruptedException {
        Number6 n = new Number6();
        new Thread(() -> {
            n.a();
        }).start();
        new Thread(() -> {
            n.b();
        }).start();
        Thread.sleep(3000);
        log.debug("end");
    }

    //情况7
    //两个不同的锁，互不干扰
    static class Number7 {
        //static synchronized 静态同步方法的锁对象是类对象
        public static synchronized void a() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("1");
        }

        //锁住的是this对象
        public synchronized void b() {
            log.debug("2");
        }
    }

    @Test
    public void t7() throws InterruptedException {
        Number7 n1 = new Number7();
        Number7 n2 = new Number7();
        new Thread(() -> {
            n1.a();
        }).start();
        new Thread(() -> {
            n2.b();
        }).start();
        Thread.sleep(3000);
        log.debug("end");
    }

    //情况8
    //即使是两个实例对象调用，但是使用的还是同一个锁对象（类对象），可同步
    static class Number8 {
        //static synchronized 静态同步方法的锁对象是类对象
        public static synchronized void a() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("1");
        }

        //锁住的是类对象
        public static synchronized void b() {
            log.debug("2");
        }
    }

    @Test
    public void t8() throws InterruptedException {
        Number8 n1 = new Number8();
        Number8 n2 = new Number8();
        new Thread(() -> {
            n1.a();
        }).start();
        new Thread(() -> {
            n2.b();
        }).start();
        Thread.sleep(3000);
        log.debug("end");
    }
}
