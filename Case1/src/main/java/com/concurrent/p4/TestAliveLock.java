package com.concurrent.p4;

import org.junit.Test;

/**
 * 饥饿
 */
public class TestAliveLock {
    @Test
    public void test1() throws InterruptedException {
        Chopstick c1 = new Chopstick("1");
        Chopstick c2 = new Chopstick("2");
        Chopstick c3 = new Chopstick("3");
        Chopstick c4 = new Chopstick("4");
        Chopstick c5 = new Chopstick("5");
        new Philosopher("苏格拉底", c1, c2).start();
        new Philosopher("柏拉图", c2, c3).start();
        new Philosopher("亚里士多德", c3, c4).start();
        new Philosopher("赫拉克利特", c4, c5).start();
        //new Philosopher("阿基米德", c5, c1).start();
        new Philosopher("阿基米德", c1, c5).start();
        while (true) ;
    }
}
