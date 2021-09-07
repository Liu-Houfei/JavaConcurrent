package com.concurrent.p3.multiGuardedObject;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j(topic = "c.TestModel_MultiGuardedObject")
public class TestModel_MultiGuardedObject {

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            new User().start();
        }
        Thread.sleep(1000);
        for (Integer id : MailBox.getIds()) {
            new Postman(id, "[content:" + id + "]").start();
        }

        Thread.sleep(5000);
    }
}