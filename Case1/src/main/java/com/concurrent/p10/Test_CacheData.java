package com.concurrent.p10;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import sun.misc.Cache;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 利用ReentrantReadWriteLock实现缓存
 */
@Slf4j(topic = "c.Test_CacheData")
public class Test_CacheData {

    class CacheData {
        //缓存对象
        private Object cacheData = "初始状态";
        //检查缓存是否失效
        private boolean cacheValid = false;
        //读写锁
        private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

        //缓存方法
        public void cacheProcess() {
            //获取读锁
            log.debug("获取读锁...");
            rwl.readLock().lock();
            if (!cacheValid) {
                //将读锁切换为写锁,因为不能直接将读锁升级成写锁,需要解锁读锁
                log.debug("释放读锁,切换到写锁");
                rwl.readLock().unlock();
                log.debug("获取写锁...");
                rwl.writeLock().lock();
                try {
                    // 判断是否有其它线程已经获取了写锁、更新了缓存, 避免重复更新
                    if (!cacheValid) {
                        log.debug("设置缓存数据...");
                        cacheData = "CacheData---web";
                        cacheValid = true;
                    }
                    //降级为读锁, 释放写锁, 这样能够让其它线程读取缓存
                    log.debug("写锁降级读锁");
                    rwl.readLock().lock();
                } finally {
                    log.debug("释放写锁");
                    rwl.writeLock().unlock();
                }
            }
            try {
                log.debug("{}", cacheData.toString());
            } finally {
                log.debug("释放读锁...");
                rwl.readLock().unlock();
            }
        }
    }

    @Test
    public void test_CacheData() {
        CacheData cacheData = new CacheData();
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                cacheData.cacheProcess();
            }).start();
        }

        while (true) ;
    }
}
