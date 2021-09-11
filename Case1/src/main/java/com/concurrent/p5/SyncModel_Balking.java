package com.concurrent.p5;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j(topic = "c.SyncModel_Balking")
public class SyncModel_Balking {
    //监控线程
    private Thread monitorThread;
    //控制线程停止
    volatile private boolean stop = false;
    //判断同一个方法是否被执行过
    private boolean start = false;

    //启动监控线程
    public void start() {
        synchronized (this) { //要加同步，因为volatile只能保证可见性，不能保证原子性
            if (start) {  //被执行过，直接结束
                return;
            }
            start = true;  //将标识设置为true
        }
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
    public void test_SyncModel_Balking() {
        SyncModel_Balking monitor = new SyncModel_Balking();
        monitor.start();
        monitor.start();
        monitor.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        monitor.stop();
    }
}
