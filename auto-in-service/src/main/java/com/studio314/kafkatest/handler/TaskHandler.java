package com.studio314.kafkatest.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskHandler {

    @Autowired
    MessageProducer messageProducer;

    public void handleMessage(String message) {
        messageProducer.sendStartMessage(message);
        System.out.println("TaskStart: " + message);
        //生成5000-15000的随机数
        int sleepTime = (int) (Math.random() * 10000 + 5000);
        // 处理任务
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        messageProducer.sendAccomplishMessage(message);
        System.out.println("TaskAccomplish: " + message);
    }

}
