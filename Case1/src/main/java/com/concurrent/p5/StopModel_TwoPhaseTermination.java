package com.concurrent.p5;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 两阶段终止模式
 */
@Slf4j(topic = "c.StopModel_TwoPhaseTermination")
public class StopModel_TwoPhaseTermination {
    //监控线程
    private Thread monitorThread;
    //控制线程停止
    volatile private boolean stop = false;

    //启动监控线程
    public void start() {
        monitorThread = new Thread(() -> {
            while (true) {
                if (stop) {
                    log.debug("料理后事...");
                    break;
                }
                try {
                    log.debug("监控...");
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
        monitorThread.start();
    }

    //停止方法
    public void stop() {
        stop = true;
        //如果虽然修改了stop，但是线程正在sleep，依然可以使用interrupt()打断sleep
        monitorThread.interrupt();
    }

    @Test
    public void test_StopModel_TwoPhaseTermination() {
        StopModel_TwoPhaseTermination monitor = new StopModel_TwoPhaseTermination();
        monitor.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        monitor.stop();
    }
}
