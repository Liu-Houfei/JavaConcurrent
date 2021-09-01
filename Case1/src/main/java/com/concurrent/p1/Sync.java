package com.concurrent.p1;

import com.concurrent.Constants;
import com.concurrent.p1.util.FileReader;
import lombok.extern.slf4j.Slf4j;

/**
 * 同步等待
 */
@Slf4j(topic = "c.Sync")
public class Sync {
    public static void main(String[] args) {
        FileReader.read(Constants.JPG_FULL_PATH);   //在主线程操作，是同步操作
        log.debug("do other things...");
    }
}
