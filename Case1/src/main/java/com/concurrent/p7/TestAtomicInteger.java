package com.concurrent.p7;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

@Slf4j(topic = "c.TestAtomicInteger")
public class TestAtomicInteger {

    /**
     * 测试 getAndIncrement 和 incrementAndGet
     */
    @Test
    public void test_getAndIncrement() {
        AtomicInteger atomicInteger = new AtomicInteger(10);
        //先获取再自增
        System.out.println(atomicInteger.getAndIncrement());  //10
        //先自增再获取
        System.out.println(atomicInteger.incrementAndGet());  //12
        System.out.println(atomicInteger.get());  //12
    }

    /**
     * 测试 getAndDecrement 和 decrementAndGet
     */
    @Test
    public void test_getAndDecrement() {
        AtomicInteger atomicInteger = new AtomicInteger(10);
        //先获取再自减
        System.out.println(atomicInteger.getAndDecrement());  //10
        //先自减再获取
        System.out.println(atomicInteger.decrementAndGet());  //8
        System.out.println(atomicInteger.get());  //8
    }

    /**
     * 测试 getAndAdd 和 addAndGet
     */
    @Test
    public void test_getAndAdd() {
        AtomicInteger atomicInteger = new AtomicInteger(10);
        //先获取再加10
        System.out.println(atomicInteger.getAndAdd(10));  //10
        //先加10再获取
        System.out.println(atomicInteger.addAndGet(10));  //30
        System.out.println(atomicInteger.get());  //30
    }

    /**
     * 测试 getAndUpdate 和 updateAndGet
     */
    @Test
    public void test_GetAndUpdate() {
        AtomicInteger atomicInteger = new AtomicInteger(10);
        /**
         * 先获取值再更新值，此处的更新操作由自己定
         * 通过lambda或者匿名内部类实现更新操作applyAsInt
         *
         *     public final int getAndUpdate(IntUnaryOperator updateFunction) {
         *         int prev, next;
         *         do {
         *             prev = get();
         *             next = updateFunction.applyAsInt(prev);
         *         } while (!compareAndSet(prev, next));
         *         return prev;
         *     }
         *
         *     @FunctionalInterface
         *     public interface IntUnaryOperator {
         *          int applyAsInt(int operand);
         *     }
         *
         */
        //lambda实现
        System.out.println(
                atomicInteger.getAndUpdate((v) -> {
                    //加10
                    v += 10;  //20
                    //乘10
                    v *= 10;  //200
                    return v;
                })
        );  //10

        System.out.println(
                atomicInteger.updateAndGet((v) -> {
                    return v;  //200
                })
        );  //200

        //匿名内部类
        System.out.println(
                atomicInteger.getAndUpdate(new IntUnaryOperator() {
                    @Override
                    public int applyAsInt(int operand) {
                        operand /= 10;  //20
                        operand -= 10;  //10
                        return operand;
                    }
                })
        );  //200
        System.out.println(
                atomicInteger.updateAndGet(new IntUnaryOperator() {
                    @Override
                    public int applyAsInt(int operand) {
                        return operand;  //10
                    }
                })
        );  //10
    }

    /**
     * 测试 getAndAccumulate 和 accumulateAndGet
     */
    @Test
    public void test_getAndAccumulate() {
        AtomicInteger atomicInteger = new AtomicInteger(10);
        /**
         *
         *     public final int getAndAccumulate(int x,
         *                                       IntBinaryOperator accumulatorFunction) {
         *         int prev, next;
         *         do {
         *             prev = get();
         *             next = accumulatorFunction.applyAsInt(prev, x);
         *         } while (!compareAndSet(prev, next));
         *         return prev;
         *     }
         *
         *     @FunctionalInterface
         *     public interface IntBinaryOperator {
         *         //Applies this operator to the given operands.
         *         int applyAsInt(int left, int right);
         *     }
         */
        System.out.println(
                atomicInteger.accumulateAndGet(2, (x, y) -> {
                    return x + y;
                })
        ); //12
        System.out.println(
                atomicInteger.accumulateAndGet(2, new IntBinaryOperator() {
                    @Override
                    public int applyAsInt(int left, int right) {
                        return left + right;
                    }
                })
        );  //14
        System.out.println(
                atomicInteger.getAndAccumulate(2, (x, y) -> {
                    return x - y;
                })
        );  //14
        System.out.println(atomicInteger.get());  //12
    }
}
