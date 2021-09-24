package com.concurrent.p9;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

@Slf4j(topic = "c.Test_TimerTask")
public class Test_TimerTask {

    @Test
    public void test_Timer_TimerTask() {
        Timer timer = new Timer();
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task-1");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task-2");
            }
        };

        //timer对象延时1秒执行任务1,2
        timer.schedule(task1, 1000);
        timer.schedule(task2, 1000);

        while (true) ;
    }

    @Test
    public void test_Timer_TimerTask_Exception() {
        Timer timer = new Timer();
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task-1");
                int i = 1 / 0;
            }
        };
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                log.debug("task-2");
            }
        };

        //timer对象延时1秒执行任务1,2
        timer.schedule(task1, 1000);
        timer.schedule(task2, 1000);

        while (true) ;
    }
}
