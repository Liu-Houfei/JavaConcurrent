package com.concurrent.p9;

import com.sun.jmx.snmp.tasks.ThreadService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j(topic = "c.Test_ThreadPoolExecutor")
public class Test_ThreadPoolExecutor {

    @Test
    public void test_newFixedThreadPool_DefaultThreadFactory() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        //任务1
        pool.execute(() -> {
            try {
                log.debug("1");
                Thread.sleep(new Random().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //任务2
        pool.execute(() -> {
            try {
                log.debug("2");
                Thread.sleep(new Random().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //任务3
        pool.execute(() -> {
            try {
                log.debug("3");
                Thread.sleep(new Random().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //任务4
        pool.execute(() -> {
            try {
                log.debug("4");
                Thread.sleep(new Random().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        while (true) ;
    }

    @Test
    public void test_newFixedThreadPool_MyThreadFactory() {
        //自定义线程工厂
        ExecutorService pool = Executors.newFixedThreadPool(2, new ThreadFactory() {
            private AtomicInteger t = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "mypool-" + t.getAndIncrement());
            }
        });
        //任务1
        pool.execute(() -> {
            try {
                log.debug("1");
                Thread.sleep(new Random().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //任务2
        pool.execute(() -> {
            try {
                log.debug("2");
                Thread.sleep(new Random().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //任务3
        pool.execute(() -> {
            try {
                log.debug("3");
                Thread.sleep(new Random().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //任务4
        pool.execute(() -> {
            try {
                log.debug("4");
                Thread.sleep(new Random().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        while (true) ;
    }

    @Test
    public void test_SynchronousQueue() {
        SynchronousQueue<Integer> integers = new SynchronousQueue<>();
        //线程1
        new Thread(() -> {
            try {
                log.debug("putting {} ", 1);  //put后会阻塞
                integers.put(1);
                log.debug("{} putted...", 1);
                log.debug("putting...{} ", 2);  ////put后会阻塞
                integers.put(2);
                log.debug("{} putted...", 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            try {
                log.debug("taking {}", 1);
                integers.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            try {
                log.debug("taking {}", 2);
                integers.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t3").start();
    }

    @Test
    public void test_newCachedThreadPool() {
        //默认线程工厂
        ExecutorService pool = Executors.newCachedThreadPool();
        //任务1
        pool.execute(() -> {
            try {
                log.debug("1");
                Thread.sleep(new Random().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //任务2
        pool.execute(() -> {
            try {
                log.debug("2");
                Thread.sleep(new Random().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //任务3
        pool.execute(() -> {
            try {
                log.debug("3");
                Thread.sleep(new Random().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //任务4
        pool.execute(() -> {
            try {
                log.debug("4");
                Thread.sleep(new Random().nextInt(5000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        while (true) ;
    }

    @Test
    public void test_newSingleThreadExecutor() {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        //虽然在执行任务过程中产生了异常，但是不会终止其他任务的执行
        pool.execute(() -> {
            log.debug("1");
            int i = 1 / 0;   //产生错误，抛出异常
        });

        pool.execute(() -> {
            log.debug("2");
        });

        pool.execute(() -> {
            log.debug("3");
        });
        while (true) ;
    }

    @Test
    public void test_submit() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Future<String> result = pool.submit(() -> {
            Thread.sleep(new Random().nextInt(5000));
            //返回字符串结果
            return "OK";
        });
        //在主线程中获取返回结果
        log.debug("返回结果：{}", result.get());
    }

    @Test
    public void test_invokeAll() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        List<Future<String>> futures = pool.invokeAll(Arrays.asList(
                () -> {
                    log.debug("1");
                    return "task-1";
                },
                () -> {
                    log.debug("2");
                    return "task-2";
                },
                () -> {
                    log.debug("3");
                    return "task-3";
                }
        ));
        //查看所有任务的返回结果
        futures.forEach((s) -> {
            try {
                log.debug(s.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void test_invokeAny() throws ExecutionException, InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        //谁先执行完就返回谁的结果
        Object any = pool.invokeAny(Arrays.asList(
                () -> {
                    log.debug("1");
                    return "task-1";
                },
                () -> {
                    log.debug("2");
                    return "task-2";
                },
                () -> {
                    log.debug("3");
                    return "task-3";
                }
        ));
        //查看返回结果
        log.debug("返回结果：{}", (String) any);
    }
}
