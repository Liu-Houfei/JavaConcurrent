package com.concurrent.p2;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "c.TestThreadSecurityAnalysis")
public class TestThreadSecurityAnalysis {

    /**
     * 1、局部变量线程安全分析
     * <p>
     * 每个线程调用f1()方法时，局部变量i会在每个线程的栈帧中被创建多份，不存在共享
     */
    public static void f1() {
        int i = 10;
        i++;
        log.debug("i={}", i);
    }

    @Test
    public void t1() throws InterruptedException {
        new Thread("t1") {
            @Override
            public void run() {
                TestThreadSecurityAnalysis.f1();
            }
        }.start();
        new Thread("t2") {
            @Override
            public void run() {
                TestThreadSecurityAnalysis.f1();
            }
        }.start();
        Thread.sleep(1000);
    }
    /**
     * 18:22:55.744 [t2] DEBUG c.TestThreadSecurityAnalysis - i=11
     * 18:22:55.742 [t1] DEBUG c.TestThreadSecurityAnalysis - i=11
     */


    /**
     * 2、局部变量为引用对象
     */

    class ThreadUnsafe {
        ArrayList<String> list = new ArrayList<>();

        public void method1() {
            for (int i = 0; i < 5; i++) {
                method2();
                method3();
            }
            print();
        }

        private void method2() {
            list.add("1");  //list为共享变量
        }

        private void method3() {
            list.remove(0);  //list为共享变量
        }

        private void print() {
            log.debug(list.toString());
        }
    }

    @Test
    public void t2() throws InterruptedException {
        ThreadUnsafe tu = new ThreadUnsafe();
        for (int i = 0; i < 2; i++) {   //哪个线程都是引用的list成员变量
            new Thread(() -> {
                log.debug("begin...");
                tu.method1();
                log.debug("end...");
            }, "t" + i).start();
        }
        Thread.sleep(3000);
    }

    /**
     * 3、将list修改为局部变量，则每个线程都有属于自己的list（线程私有）
     */
    class ThreadSafe {
        //ArrayList<String> list = new ArrayList<>();

        public final void method1() {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                method2(list);
                method3(list);
            }
            print(list);
        }

        private void method2(List<String> list) {
            list.add("1");  //list为共享变量
        }

        private void method3(List<String> list) {
            list.remove(0);  //list为共享变量
        }

        public void print(List<String> list) {
            log.debug(list.toString());
        }
    }

    @Test
    public void t3() throws InterruptedException {
        ThreadSafe tu = new ThreadSafe();
        for (int i = 0; i < 2; i++) {   //哪个线程都是引用的list成员变量
            new Thread(() -> {
                log.debug("begin...");
                tu.method1();
                log.debug("end...");
            }).start();
        }
        Thread.sleep(3000);
    }
    /**
     * 20:42:13.695 [t1] DEBUG c.TestThreadSecurityAnalysis - begin...
     * 20:42:13.699 [t1] DEBUG c.TestThreadSecurityAnalysis - []
     * 20:42:13.699 [t1] DEBUG c.TestThreadSecurityAnalysis - end...
     * 20:42:13.695 [t0] DEBUG c.TestThreadSecurityAnalysis - begin...
     * 20:42:13.700 [t0] DEBUG c.TestThreadSecurityAnalysis - []
     * 20:42:13.700 [t0] DEBUG c.TestThreadSecurityAnalysis - end...
     */


    /**
     * 4、局部变量-暴露引用
     * 把局部变量引用的对象暴露给外部
     */
    class ThreadSafe2 {
        public final void method1() {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                method2(list);
                method3(list);
            }
            print(list);
        }

        public void method2(List<String> list) {
            list.add("1");
        }

        public void method3(List<String> list) {
            list.remove(0);
        }

        public void print(List<String> list) {
            log.debug(list.toString());
        }
    }

    @Test
    public void t4() throws InterruptedException {
        ThreadSafe2 ts2 = new ThreadSafe2();
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                log.debug("begin...");
                ts2.method1();
                log.debug("end...");
            }).start();
        }
        Thread.sleep(3000);
    }

    /**
     * 运行结果：
     * 21:35:41.444 [Thread-1] DEBUG c.TestThreadSecurityAnalysis - begin...
     * 21:35:41.444 [Thread-0] DEBUG c.TestThreadSecurityAnalysis - begin...
     * 21:35:41.448 [Thread-1] DEBUG c.TestThreadSecurityAnalysis - []
     * 21:35:41.449 [Thread-1] DEBUG c.TestThreadSecurityAnalysis - end...
     * 21:35:41.449 [Thread-0] DEBUG c.TestThreadSecurityAnalysis - []
     * 21:35:41.449 [Thread-0] DEBUG c.TestThreadSecurityAnalysis - end...
     */


    /**
     * 5、为ThreadSafe2添加子类，子类覆盖method3方法
     */
    class ThreadSafeSubClass extends ThreadSafe2 {

        //会出先线程安全问题，将局部变量list的引用暴露给子类中的其他线程
        @Override
        public void method3(List<String> list) {
            //创建一个新线程执行删除list集合头元素
            new Thread(() -> {
                list.remove(0);
            }).start();
        }
    }

    @Test
    public void t5() throws InterruptedException {
        ThreadSafeSubClass tsSub = new ThreadSafeSubClass();
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                log.debug("begin...");
                tsSub.method1();
                log.debug("end...");
            }).start();
        }
        Thread.sleep(2000);
    }

}
