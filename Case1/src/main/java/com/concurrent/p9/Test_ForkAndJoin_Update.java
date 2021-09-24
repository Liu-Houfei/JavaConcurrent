package com.concurrent.p9;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

@Slf4j(topic = "c.Test_ForkAndJoin_Update")
public class Test_ForkAndJoin_Update {

    @Test
    public void test() {
        ForkJoinPool pool = new ForkJoinPool(4);
        int result = pool.invoke(new AddTask(1, 5));
        log.debug("最终结果：{}", result);
    }
}

@Slf4j(topic = "c.AddTask")
class AddTask extends RecursiveTask<Integer> {

    private int begin;
    private int end;

    public AddTask(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public String toString() {
        return "{" + begin + "," + end + '}';
    }

    @Override
    protected Integer compute() {
        //递归出口
        if (begin == end) {
            log.debug("join() {}", begin);
            return begin;
        }
        //中点
        int mid = (begin + end) / 2;
        AddTask task1 = new AddTask(begin, mid);
        AddTask task2 = new AddTask(mid + 1, end);
        //fork
        task1.fork();
        task2.fork();
        log.debug("fork() {} + {} = ?", task1, task2);
        //模拟运算
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //join
        int result = task1.join() + task2.join();
        log.debug("join() {} + {} = {}", task1, task2, result);
        return result;
    }
}
