package com.concurrent.p3.multi_guardedObject;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户线程，获取邮件
 */
@Slf4j(topic = "c.User")
public class User extends Thread {
    @Override
    public void run() {
        //收信
        MailGuardedObject mailGuardedObject = MailBox.createMailGuardedObject();
        log.debug("开始收信 id:{}", mailGuardedObject.getId());
        Object mail = mailGuardedObject.get(5000);
        log.debug("收信 id:{},内容:{}", mailGuardedObject.getId(), mail);
    }
}
