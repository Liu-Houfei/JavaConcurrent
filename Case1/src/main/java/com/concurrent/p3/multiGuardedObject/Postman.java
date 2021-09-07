package com.concurrent.p3.multiGuardedObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Postman")
public class Postman extends Thread {

    private int id;
    private String mail;

    public Postman(int id, String mail) {
        this.id = id;
        this.mail = mail;
    }

    @Override
    public void run() {
        MailGuardedObject mailGuardedObject = MailBox.getMailGuardedObject(id);
        log.debug("送信id:{}，内容:{}", mailGuardedObject.getId(), mail);
        mailGuardedObject.complete(mail);
    }
}
