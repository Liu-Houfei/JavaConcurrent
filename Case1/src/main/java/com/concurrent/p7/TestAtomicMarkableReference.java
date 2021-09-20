package com.concurrent.p7;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicMarkableReference;

@Slf4j(topic = "c.TestAtomicMarkableReference")
public class TestAtomicMarkableReference {
    @Test
    public void test_AtomicMarkableReference() {
        //创建垃圾袋对象
        GarbageBag bag = new GarbageBag("已装满");
        //创建原子引用对象
        AtomicMarkableReference<GarbageBag> ref =
                new AtomicMarkableReference<>(bag, true);

        log.debug("start...");
        GarbageBag prev = ref.getReference();
        log.debug(prev.toString());

        //保洁线程更换垃圾袋
        new Thread(() -> {
            log.debug("start...");
            ref.compareAndSet(prev, new GarbageBag("空垃圾袋"), true, false);
            log.debug("保洁更换垃圾袋...");
        }, "保洁").start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("是否更换垃圾袋?");
        boolean isSucceed =
                ref.compareAndSet(prev, new GarbageBag("空垃圾袋"), true, false);
        log.debug("换了吗？{}", isSucceed);
        log.debug(ref.getReference().toString());
    }
}

class GarbageBag {
    private String status;

    public GarbageBag(String status) {
        this.status = status;
    }
}
