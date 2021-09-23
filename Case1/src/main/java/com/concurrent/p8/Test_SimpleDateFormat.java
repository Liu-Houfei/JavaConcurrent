package com.concurrent.p8;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

@Slf4j(topic = "c.Test_SimpleDateFormat")
public class Test_SimpleDateFormat {

    /**
     * SimpleDateFormat可变类会造成线程安全问题
     */
    @Test
    public void test1() throws InterruptedException {
        //定义共享可变类对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //10个线程访问共享变量
        for (int i = 0; i < 10; i++) {
            //出异常 java.lang.NumberFormatException: multiple points
            new Thread(() -> {
                try {
                    log.debug(sdf.parse("2021-09-20") + "");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        Thread.sleep(1000);
    }

    /**
     * synchronized上锁，可以解决问题，但是效率低
     */
    @Test
    public void test2() throws InterruptedException {
        //定义共享可变类对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //10个线程访问共享变量
        for (int i = 0; i < 10; i++) {
            //出异常
            new Thread(() -> {
                //添加synchronized后可以正常运行
                synchronized (sdf) {
                    try {
                        log.debug(sdf.parse("2021-09-20") + "");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        Thread.sleep(1000);
    }

    /**
     * 使用DateTimeFormatter不可变类
     */
    @Test
    public void test3() throws InterruptedException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //10个线程访问共享变量
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                TemporalAccessor parse = dtf.parse("2021-09-20");
                log.debug(parse + "");
            }).start();
        }
        Thread.sleep(1000);
    }

}
