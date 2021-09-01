package com.concurrent.p1;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;


/**
 * 创建线程
 */
@Slf4j(topic = "c.TestCreateThread")
public class TestCreateThread {
    //Thread匿名内部类创建线程
    @Test
    public void t1() {
        Thread t1 = new Thread() {
            @Override
            public void run() {
                log.debug("running...");
            }
        };
        t1.setName("t1");
        t1.start();

        //匿名内部类创建线程
        Thread t2 = new Thread("t2") {
            @Override
            public void run() {
                log.debug("running...");
            }
        };
        t2.start();
    }

    //使用Runnable配合Thread,可实现线程和任务分离
    @Test
    public void t2() {
        //Runnable接口的匿名内部类实现run()方法
        Runnable r = new Runnable() {
            @Override
            public void run() {
                log.debug("running...");
            }
        };
        Thread t1 = new Thread(r);
        t1.setName("t1");
        t1.start();
        log.debug("running...");
    }

    //Runnable是一个函数式接口，可以lambda表达式实现run()方法
    @Test
    public void t3() {
        //任务和线程分离
        Runnable r = () -> {
            log.debug("lambda_running...");
        };
        Thread t1 = new Thread(r);
        t1.setName("t1");
        t1.start();

        //或者，任务和线程不分离
        Thread t2 = new Thread(() -> {
            log.debug("lambda_running...");
        });
        t2.setName("t2");
        t2.start();
        log.debug("running...");

        //或者，直接在构造方法中给线程命名
        Thread t3 = new Thread(() -> {
            log.debug("lambda_running...");
        }, "t3");
        t3.start();
        log.debug("running...");
    }

    //FutureTask配合Thread
    //Future能够接收Callable类型的参数，用来处理有返回结果的情况
    //public class FutureTask<V> implements RunnableFuture<V>
    @Test
    public void t4() {
        //创建task任务对象，参数为Callable对象
        FutureTask<Integer> task = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                //执行完任务之后，返回一个Integer值
                //模拟执行任务并返回100
                log.debug("running");
                Thread.sleep(1000);
                return 100;
            }
        });
        //创建线程
        //因为FutureTask实现了Runnable接口，因此可以当作Thread的参数
        Thread t1 = new Thread(task, "t1");
        t1.start();
        //在主线程获取task对象的call()方法返回值，主线程会在此处阻塞
        try {
            log.debug("{}", task.get());    //09:02:03.590 [main] DEBUG c.TestCreateThread - 100
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


}
