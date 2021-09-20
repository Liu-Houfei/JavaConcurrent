package com.concurrent.p7;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 利用Unsafe创建自己的AtomicInteger
 */
@Slf4j(topic = "c.Test_MyAtomicInteger")
public class Test_MyAtomicInteger {

    @Test
    public void test_MyAtomicInteger() {
        //定义共享账户变量
        MyAccount myAccount = new MyAccount(100000);
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            threadList.add(new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    myAccount.decrement(1000);
                }
            }));
        }
        threadList.forEach(t -> t.start());
        threadList.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        //输出账户最终余额
        log.debug("余额：{}", myAccount.getBalance());
    }

}

class MyAtomicInteger {
    //贡献变量
    private volatile int value;
    //成员对象偏移量
    private static long valueOffset;
    //unsafe对象
    private static Unsafe unsafe;

    static {
        try {
            //反射获取unsafe对象
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
            //获取偏移量
            valueOffset = unsafe.objectFieldOffset(MyAtomicInteger.class.getDeclaredField("value"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public MyAtomicInteger(int value) {
        this.value = value;
    }

    //获取数值
    public int getValue() {
        return value;
    }

    //cas减操作
    public boolean decrement(int v) {
        int prev, next;
        do {
            prev = getValue();
            next = prev - v;
        } while (!unsafe.compareAndSwapInt(this, valueOffset, prev, next));
        return true;
    }
}

//账户类
class MyAccount {
    private MyAtomicInteger balance;

    public MyAccount(int balance) {
        this.balance = new MyAtomicInteger(balance);
    }

    public void decrement(int value) {
        balance.decrement(value);
    }

    public int getBalance() {
        return balance.getValue();
    }
}