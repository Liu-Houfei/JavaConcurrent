package com.concurrent.p7;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Unsafe类使用
 */
@Slf4j(topic = "c.Test_Unsafe")
public class Test_Unsafe {

    /**
     * Unsafe 对象提供了非常底层的，操作内存、线程的方法，Unsafe 对象不能直接调用，
     * 只能通过反射获得
     * private static final Unsafe theUnsafe;
     */
    @Test
    public void test_createUnsafeObj() throws NoSuchFieldException, IllegalAccessException {
        //反射获取私有的成员变量theUnsafe
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        //暴力反射，可以访问private
        theUnsafe.setAccessible(true);
        //静态不需要传递对象
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
        System.out.println(unsafe);
    }

    /**
     * Unsafe的Cas操作
     */
    @Test
    public void test_UnsafeCas() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);
            //获取域偏移量
            long idOffset = unsafe.objectFieldOffset(Student.class.getDeclaredField("id"));
            long nameOffset = unsafe.objectFieldOffset(Student.class.getDeclaredField("name"));
            //cas修改student对象的id,name
            Student student = new Student();
            System.out.println(student);
            unsafe.compareAndSwapInt(student, idOffset, 0, 1);
            unsafe.compareAndSwapObject(student, nameOffset, null, "张三");
            System.out.println(student);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * Student{id=0, name='null'}
         * Student{id=1, name='张三'}
         */
    }

    class Student {
        private volatile int id;
        private volatile String name;

        @Override
        public String toString() {
            return "Student{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
