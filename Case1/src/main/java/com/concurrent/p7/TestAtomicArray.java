package com.concurrent.p7;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j(topic = "c.TestAtomicArray")
public class TestAtomicArray {

    /**
     * 参数1，提供数组、可以是线程不安全数组或线程安全数组
     * 参数2，获取数组长度的方法
     * 参数3，自增方法，回传 array, index
     * 参数4，打印数组的方法
     * <p>
     * 函数式接口说明
     * supplier  提供者  无中生有  ()->结果
     * function  函数  一个参数一个结果  (参数)->结果
     * BiFunction  函数   两个参数一个结果  （参数1，参数2）->结果
     * consumer  消费者  一个参数没有结果  (参数)->void
     * BiConsumer  消费者  两个参数没有结果  （参数1，参数2）->void
     *
     * @FunctionalInterface public interface Supplier<T> {
     * T get();
     * }
     * @FunctionalInterface public interface Function<T, R> {
     * R apply(T t);
     * }
     * @FunctionalInterface public interface BiConsumer<T, U> {
     * void accept(T t, U u);
     * }
     * @FunctionalInterface public interface Consumer<T> {
     * void accept(T t);
     * }
     */


    private static <T> void demo(
            Supplier<T> arraySupplier,
            Function<T, Integer> lengthFun,
            BiConsumer<T, Integer> putConsumer,
            Consumer<T> printConsumer
    ) {
        List<Thread> ts = new ArrayList<>();
        //创建数组
        T array = arraySupplier.get();
        //获取数组长度
        int length = lengthFun.apply(array);
        for (int i = 0; i < length; i++) {
            //每个线程对数组做1000次操作
            ts.add(
                    new Thread(() -> {
                        for (int j = 0; j < 10000; j++) {
                            putConsumer.accept(array, j % length);
                        }
                    })
            );
        }
        //启动所有线程
        ts.forEach(t -> t.start());
        //等待所有线程执行结束
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //输出数组
        printConsumer.accept(array);
    }

    /**
     * 多线程下，一个普通数组是没有线程安全性的
     */
    @Test
    public void test_array() {
        demo(
                () -> new int[10],
                array -> array.length,
                (array, index) -> {
                    array[index]++;
                },
                (array) -> {
                    System.out.println(Arrays.toString(array));
                }
        );
        //[9682, 9658, 9662, 9652, 9660, 9667, 9682, 9672, 9671, 9667]
    }

    /**
     * 原子数组 AtomicIntegerArray
     */
    @Test
    public void test_AtomicIntegerArray() {
        demo(
                () -> new AtomicIntegerArray(10),
                atomicArray -> atomicArray.length(),
                (atomicIntegerArray, index) -> {
                    atomicIntegerArray.getAndIncrement(index);
                },
                atomicIntegerArray -> {
                    System.out.println(atomicIntegerArray.toString());
                }
        );
        //[10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000]
    }
}
