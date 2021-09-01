package com.concurrent.p1;

import com.concurrent.Constants;
import com.concurrent.p1.util.FileReader;
import com.concurrent.p1.util.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * 异步不等待
 */
@Slf4j(topic = "c.Async")
public class Async {
    public static void main(String[] args) {
        //使用匿名内部类
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileReader.read(Constants.JPG_FULL_PATH);
            }
        }).start();

        //使用lambda表达式
        new Thread(() -> {
            FileReader.read(Constants.JPG_FULL_PATH_2);
        }).start();

        log.debug("do other things...");
    }
}
