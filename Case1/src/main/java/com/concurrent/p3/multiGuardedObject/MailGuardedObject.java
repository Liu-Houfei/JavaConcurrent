package com.concurrent.p3.multiGuardedObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.MailGuardedObject")
public class MailGuardedObject {
    //标识 GuardedObject resp
    private Integer id;

    private Object resp;

    public MailGuardedObject(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }


    //产生结果
    public void complete(Object obj) {
        synchronized (this) {
            this.resp = obj;
            this.notifyAll();
        }
    }

    //获取结果
    public Object get() {
        synchronized (this) {
            while (resp == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return resp;
        }
    }

    //超时获取结果
    public Object get(long timeout) {
        synchronized (this) {
            long begin = System.currentTimeMillis();
            long passTime = 0;  //经历时间
            while (resp == null) {
                long waitTime = timeout - passTime;
                if (waitTime <= 0) {
                    break;
                }
                try {
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                passTime = System.currentTimeMillis() - begin;
            }
            return resp;
        }
    }

}
