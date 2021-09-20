package com.concurrent.p7;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j(topic = "c.Test_AtomicLong_LongAdder")
public class Test_AtomicLong_LongAdder {

    /**
     * @param supplier ()->(结果)
     * @param consumer (参数)->()
     * @param <T>
     */
    public static <T> void demo(Supplier<T> supplier, Consumer<T> consumer) {
        T adder = supplier.get();  //累加器，初始值=0
        List<Thread> threadList = new ArrayList<>();
        //添加计时器
        long start = System.currentTimeMillis();
        //创建4个线程，每个线程累加500000次
        for (int i = 0; i < 4; i++) {
            threadList.add(new Thread(() -> {
                for (int j = 0; j < 500000; j++) {
                    consumer.accept(adder);
                }
            }));
        }
        //启动每个线程
        threadList.forEach(t -> {
            t.start();
        });
        //等待每个线程执行结束
        threadList.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //查看累加器
        log.debug(adder.toString() + ",耗时：" + (System.currentTimeMillis() - start));
    }

    //测试AtomicLong累加效率
    @Test
    public void test_AtomicLong() {
        //10:27:11.592 [main] DEBUG c.Test_AtomicLong_LongAdder - 2000000,耗时：33
        demo(
                () -> new AtomicLong(0),
                (addr) -> {
                    addr.getAndIncrement();
                }
        );
    }

    //测试LongAdder累加效率
    //LongAdder比AtomicLong累加效率高
    @Test
    public void test_LongAddr() {
        //10:28:00.303 [main] DEBUG c.Test_AtomicLong_LongAdder - 2000000,耗时：25
        demo(
                () -> new LongAdder(),
                (addr) -> {
                    addr.increment();
                }
        );
    }
}
