package com.concurrent.p9;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@Slf4j(topic = "c.Test_ForkAndJoin")
public class Test_ForkAndJoin {

    @Test
    public void test_forkAndJoin_SimpleAdd() {
        ForkJoinPool pool = new ForkJoinPool(4);
        Integer integer = pool.invoke(new MyTask(5));
        log.debug("最终计算结果：{}", integer);
    }
}

@Slf4j(topic = "c.MyTask")
class MyTask extends RecursiveTask<Integer> {

    private int n;

    public MyTask(int n) {
        this.n = n;
    }

    @Override
    protected Integer compute() {
        if (n == 1) {
            return 1;
        }
        MyTask t1 = new MyTask(n - 1);  //类似递归，让每一个线程执行单次递归
        t1.fork();
        log.debug("当前n:{}", n);
        //模拟执行
        try {
            log.debug("开始计算...");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("当前线程计算结果:{}", t1.join());
        int result = n + t1.join();
        log.debug("当前result:{}", result);
        return result;
    }
}
