package com.concurrent.p3.multiGuardedObject;

import lombok.extern.slf4j.Slf4j;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * 中间解耦类
 * 解耦结果的产生者和结果获取者
 * RPC框架常用
 */
@Slf4j(topic = "c.MailBox")
public class MailBox {
    //HashTable是线程安全的
    private static Map<Integer, MailGuardedObject> mailBoxs = new Hashtable<>();

    //唯一标识
    private static int id = 1;

    //id递增
    private synchronized static int generateId() {
        return id++;
    }

    //产生MailGuardedObject
    public static MailGuardedObject createMailGuardedObject() {
        //创建MailGuardedObject对象，id自增
        MailGuardedObject mailGuardedObject = new MailGuardedObject(generateId());
        mailBoxs.put(mailGuardedObject.getId(), mailGuardedObject);
        return mailGuardedObject;
    }

    //根据id获取MailGuardedObject
    public static MailGuardedObject getMailGuardedObject(int id) {
        return mailBoxs.remove(id);
    }

    //返回编id集合
    public static Set<Integer> getIds() {
        return mailBoxs.keySet();
    }
}
