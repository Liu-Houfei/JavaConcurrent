package com.concurrent.p9;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j(topic = "c.Test_ThreadPoolStatus")
public class Test_ThreadPoolStatus {

    @Test
    public void test_shutdown() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Future<Integer> result1 = pool.submit(() -> {
            try {
                log.debug("task 1 running...");
                Thread.sleep(1000);
                log.debug("task 1 finished...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        });
        Future<Integer> result2 = pool.submit(() -> {
            try {
                log.debug("task 2 running...");
                Thread.sleep(1000);
                log.debug("task 2 finished...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 2;
        });
        Future<Integer> result3 = pool.submit(() -> {
            try {
                log.debug("task 3 running...");
                Thread.sleep(1000);
                log.debug("task 3 finished...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 3;
        });
        //调用shutdown方法
        log.debug("shutdown...");
        pool.shutdown();
        log.debug("other...");


        while (true) ;
    }

    @Test
    public void test_shutdownNow() {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Future<Integer> result1 = pool.submit(() -> {
            try {
                log.debug("task 1 running...");
                Thread.sleep(1000);
                log.debug("task 1 finished...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        });
        Future<Integer> result2 = pool.submit(() -> {
            try {
                log.debug("task 2 running...");
                Thread.sleep(1000);
                log.debug("task 2 finished...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 2;
        });
        Future<Integer> result3 = pool.submit(() -> {
            try {
                log.debug("task 3 running...");
                Thread.sleep(1000);
                log.debug("task 3 finished...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 3;
        });
        //队列中没有执行的任务会返回
        log.debug("shutdownNow...");
        List<Runnable> runnables = pool.shutdownNow();
        runnables.forEach((s) -> log.debug("任务：" + s));
        log.debug("other...");
        while (true) ;
    }


}
