package com.concurrent.p3;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j(topic = "c.TestWaitNotifyStep")
public class TestWaitNotifyStep {

    static final Object room = new Object();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    @Test
    public void testStep1() throws InterruptedException {
        //线程1
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        }, "小南").start();

        //线程2-6
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                synchronized (room) {
                    log.debug("可以开始干活了");
                }
            }, "其它人").start();
        }

        //线程7
        Thread.sleep(1000);
        new Thread(() -> {
            // 这里能不能加 synchronized (room)？
            //此处不能加synchronized
            hasCigarette = true;
            log.debug("烟到了噢！");
        }, "送烟的").start();

        Thread.sleep(100000);
    }

    @Test
    public void testStep2() throws InterruptedException {
        //使用wait-notify优化
        //小南线程
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    //没有烟就等待，此时可以释放锁
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        }, "小南").start();

        //其他人线程
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                synchronized (room) {
                    log.debug("可以开始干活了");
                }
            }, "其他人").start();
        }

        //送烟的
        new Thread(() -> {
            synchronized (room) {
                hasCigarette = true;
                log.debug("烟到了噢！");
                //唤醒正在等待的线程
                room.notify();
            }
        }, "送烟的").start();

        Thread.sleep(10000);
    }

    @Test
    public void testStep3() throws InterruptedException {
        //小南线程
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    //没有烟就等待，此时可以释放锁
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没烟干不了");
                }
            }
        }, "小南").start();

        //小女线程
        new Thread(() -> {
            synchronized (room) {
                log.debug("有外卖没？[{}]", hasCigarette);
                if (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    //没有外卖就等待，此时可以释放锁
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有外卖没？[{}]", hasCigarette);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没外卖干不了");
                }
            }
        }, "小女").start();

        //送外卖的
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (room) {
                hasTakeout = true;
                log.debug("外卖到了");
                //room.notify();
                room.notifyAll();
            }
        }, "送外卖的").start();

        Thread.sleep(10000);
    }


    @Test
    public void testStep5() throws InterruptedException {
        //小南线程
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                //将if改为while多次判断
                while(!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    //没有烟就等待，此时可以释放锁
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没烟干不了");
                }
            }
        }, "小南").start();

        //小女线程
        new Thread(() -> {
            synchronized (room) {
                log.debug("有外卖没？[{}]", hasTakeout);
                while(!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    //没有外卖就等待，此时可以释放锁
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有外卖没？[{}]", hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没外卖干不了");
                }
            }
        }, "小女").start();

        //送外卖的
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (room) {
                hasTakeout = true;
                log.debug("外卖到了");
                //room.notify();
                room.notifyAll();
            }
        }, "送外卖的").start();

        Thread.sleep(10000);
    }




}
