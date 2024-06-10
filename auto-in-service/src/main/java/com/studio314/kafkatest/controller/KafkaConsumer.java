package com.studio314.kafkatest.controller;

import com.studio314.kafkatest.handler.TaskAllocator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    @Autowired
    private TaskAllocator taskAllocator;

    @KafkaListener(topics = "wait", groupId = "testGroup")
    public void onNormalMessage(String message) {
        System.out.println("Received Message: " + message);
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("Received Message: " + message);
        while (true) {
            // 检查TaskAllocator是否可以分配一个新的任务
            if (taskAllocator.canAllocate()) {
                // 提交一个新的任务到线程池
                taskAllocator.allocate(message);
                break;
            } else {
                // 使当前线程休眠
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

}
