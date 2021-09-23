package com.concurrent.p9;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j(topic = "c.Test_ThreadPoolStarvation")
public class Test_ThreadPoolStarvation {

    static final List<String> MENU = Arrays.asList("地三鲜", "辣子鸡", "可乐");
    static Random RANDOM = new Random();

    static String cooking() {
        return MENU.get(RANDOM.nextInt(MENU.size()));
    }

    /**
     * 饥饿现象演示
     */
    @Test
    public void test_starvation() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        //1个线程点餐，1个线程做菜，不会饥饿
        pool.execute(() -> {
            log.debug("开始点餐");
            Future<String> future = pool.submit(() -> {
                log.debug("做菜...");
                return cooking();
            });
            try {
                log.debug("上菜:{}", future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        pool.execute(() -> {
            log.debug("开始点餐");
            Future<String> future = pool.submit(() -> {
                log.debug("做菜...");
                return cooking();
            });
            try {
                log.debug("上菜:{}", future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        while (true) ;
    }

    /**
     * 饥饿问题的解决
     */
    @Test
    public void test_starvationResolving() {
        //2个点餐线程
        ExecutorService waiter = Executors.newFixedThreadPool(2);
        //2个做饭线程
        ExecutorService cooker = Executors.newFixedThreadPool(2);

        waiter.execute(() -> {
            log.debug("开始点餐...");
            Future<String> future = cooker.submit(() -> {
                log.debug("做饭...");
                return cooking();
            });
            try {
                log.debug("上菜：{}", future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        waiter.execute(() -> {
            log.debug("开始点餐...");
            Future<String> future = cooker.submit(() -> {
                log.debug("做饭...");
                return cooking();
            });
            try {
                log.debug("上菜：{}", future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

        while (true) ;
    }
}
