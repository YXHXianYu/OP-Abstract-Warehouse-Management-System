package com.studio314.kafkatest.handler;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class TaskAllocator {

    @Value("${ware.task.robot.num}")
    private int robotNum;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(robotNum);
    }

    @Autowired
    private TaskHandler taskHandler;

    private ExecutorService executorService;

    public boolean canAllocate() {
        // 检查线程池中是否有空闲的线程
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
        return threadPoolExecutor.getActiveCount() < threadPoolExecutor.getMaximumPoolSize();
    }

    public void allocate(String message) {
        executorService.submit(() -> taskHandler.handleMessage(message));
    }

}
