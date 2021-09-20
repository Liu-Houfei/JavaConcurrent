package com.concurrent.p7;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

@Slf4j(topic = "c.TestAtomicReferenceFieldUpdater")
public class TestAtomicReferenceFieldUpdater {

    @Test
    public void test_AtomicReferenceFieldUpdater() {
        //创建原子更新器对象
        AtomicReferenceFieldUpdater updater =
                AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");
        Student stu = new Student(null);
        System.out.println(updater.compareAndSet(stu, null, "张三"));  //true
    }
}

class Student {
    volatile String name;

    public Student(String name) {
        this.name = name;
    }
}
