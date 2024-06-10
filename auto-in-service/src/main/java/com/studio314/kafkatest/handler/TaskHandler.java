package com.studio314.kafkatest.handler;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class TaskHandler {

    @Autowired
    MessageProducer messageProducer;

    public void handleMessage(String message) {
        //将message转为map
        HashMap<String, Object> m = JSON.parseObject(message, HashMap.class);
        //装货
        m.put("state", 9);
        messageProducer.sendAccomplishMessage(JSON.toJSONString(m));
        System.out.println("TaskStart: " + message);
        int sleepTime = 1000;
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //入库
        m.put("state", 3);
        messageProducer.sendAccomplishMessage(JSON.toJSONString(m));
        System.out.println("TaskLoading: " + JSON.toJSONString(m));
        sleepTime = 4000;
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //运输
        m.put("state", 10);
        messageProducer.sendAccomplishMessage(JSON.toJSONString(m));
        System.out.println("TaskTransport: " + JSON.toJSONString(m));
        sleepTime = (int) (Math.random() * 10000 + 5000);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //上架
        m.put("state", 11);
        messageProducer.sendAccomplishMessage(JSON.toJSONString(m));
        sleepTime = 5000;
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        m.put("state", 4);
        messageProducer.sendAccomplishMessage(JSON.toJSONString(m));
        System.out.println("TaskAccomplish: " + JSON.toJSONString(m));
    }

}
