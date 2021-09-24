package com.concurrent.p9;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "c.Test_ScheduledThreadService_Application")
public class Test_ScheduledThreadService_Application {

    //周四18:00：00 定时执行任务
    @Test
    public void test() {
        //计算初始延时时间（当前时间到周四18:00:00的时间）
        LocalDateTime now = LocalDateTime.now();    //获取当前时间
        LocalDateTime time =
                now.withHour(18).withMinute(0).withSecond(0).withNano(0).with(DayOfWeek.THURSDAY);
        //判断：如果当前时间已经超过周四，则要加1周
        if (now.compareTo(time) > 0) {
            time = time.plusWeeks(1);
        }
        long initalDelay = Duration.between(now, time).toMillis();

        //计算每周时间间隔
        long period = 1000 * 60 * 60 * 24 * 7;

        System.out.println(initalDelay);
        System.out.println(period);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            log.debug("执行任务...");
        }, initalDelay, period, TimeUnit.MILLISECONDS);

        while (true) ;
    }
}
