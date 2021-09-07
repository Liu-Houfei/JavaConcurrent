package com.concurrent.p3;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 同步模式-保护性暂停
 */
@Slf4j(topic = "c.TestModel_GuardedObject")
public class TestModel_GuardedObject {
    @Test
    public void testGuardedObject() {
        //定义共享对象
       GuardedObject guardedObject = new GuardedObject();

        //线程1执行下载
        Thread t1 = new Thread(() -> {
            log.debug("正在下载，请稍等...");
            List<String> list = (ArrayList<String>) WebDownload.download();
            guardedObject.complete(list);
            log.debug("已经完成下载");
        }, "t1");
        t1.start();

        //线程2获取下载结果
        Thread t2 = new Thread(() -> {
            log.debug("获取下载内容...");
            //List<String> list = (ArrayList<String>) guardedObject.get();
            List<String> list = (ArrayList<String>) guardedObject.get(2000);
            list.forEach((s) -> {
                log.debug(s);
            });
        }, "t2");
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 线程同步对象：GuardedObject
 */

class GuardedObject {
    //同步对象
    private Object resp;

    //获取结果
    public Object get() {
        synchronized (this) {
            //循环判断，如果当前结果为空则等待；不为空则返回结果
            while (this.resp == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return resp;
        }
    }

    //获取结果扩展-添加获取超时参数
    //timeout 最大等待时间
    public Object get(long timeout) {
        synchronized (this) {
            long passedTime = 0;
            long beginTime = System.currentTimeMillis();
            //循环判断，如果当前结果为空则等待；不为空则返回结果
            while (this.resp == null) {
                long waitTime = timeout - passedTime;
                //如果经历时间超过超时时间，返回
                if (waitTime <= 0) {
                    break;
                }
                try {
                    this.wait(waitTime);  //避免虚假唤醒15:00:01
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                passedTime = System.currentTimeMillis() - beginTime;
            }
            return resp;
        }
    }

    //产生结果
    public void complete(Object object) {
        synchronized (this) {
            this.resp = object;
            //唤醒所有等待线程
            this.notifyAll();
        }
    }
}

/**
 * 网页下载器：WebDownload
 */
@Slf4j(topic = "c.WebDownload")
class WebDownload {
    public static Object download() {
        List<String> list = new ArrayList<>();
        //模拟5秒内完成下载
        for (int i = 0; i < 5; i++) {
            log.debug("还需要{}秒完成下载", (5 - i));
            try {
                Thread.sleep(1000);
                list.add("Java" + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
