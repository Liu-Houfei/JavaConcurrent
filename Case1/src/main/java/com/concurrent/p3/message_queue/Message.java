package com.concurrent.p3.message_queue;

import lombok.extern.slf4j.Slf4j;

/**
 * 消息的JavaBean
 */
@Slf4j(topic = "c.Message")
public class Message {
    private static Integer id = 1;
    private String message;

    private static Integer generateId() {
        return id++;
    }

    public Message(String message) {
        generateId();
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id + "\t" +
                "message='" + message + '\'' +
                '}';
    }
}
