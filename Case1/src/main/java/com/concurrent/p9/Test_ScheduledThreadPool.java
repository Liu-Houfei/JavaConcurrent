package com.concurrent.p9;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.*;

@Slf4j(topic = "c.Test_ScheduledThreadPool")
public class Test_ScheduledThreadPool {

    //ScheduledThreadPool延时执行任务
    @Test
    public void test_ScheduledThreadPool_Delay() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        pool.schedule(() -> {
            log.debug("task-1");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 1000, TimeUnit.MILLISECONDS);
        pool.schedule(() -> {
            log.debug("task-2");
            int i = 1 / 0;
        }, 1000, TimeUnit.MILLISECONDS);

        while (true) ;
    }

    //ScheduledThreadPool定时执行任务
    @Test
    public void test_ScheduledThreadPool_scheduleAtFixedRate() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        pool.scheduleAtFixedRate(() -> {
            log.debug("running...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);
        while (true) ;
    }

    @Test
    public void test_ScheduledThreadPool_scheduleWithFixedDelay() {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
        pool.scheduleWithFixedDelay(() -> {
            log.debug("running...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);
        while (true) ;
    }

    //可以使用try.catch 或者 Future<T> 捕获异常
    @Test
    public void test_ScheduledThreadPool_catchException() throws ExecutionException, InterruptedException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.schedule(() -> {
            log.debug("task-1");
            //1.使用try/catch捕获异常
            try {
                int i = 1 / 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1000, TimeUnit.MILLISECONDS);

        //2.使用Future<T>对象
        ScheduledFuture<Integer> future = executor.schedule(() -> {
            log.debug("task-2");
            int i = 1 / 0;
            return 0;
        }, 1000, TimeUnit.MILLISECONDS);

        log.debug("{}", future.get());
    }
}
