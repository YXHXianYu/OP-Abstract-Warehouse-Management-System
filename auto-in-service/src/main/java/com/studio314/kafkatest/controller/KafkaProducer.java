package com.studio314.kafkatest.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static int count = 0;

    @GetMapping("/kafka/normal/{message}")
    public String sendNormalMessage(@PathVariable("message") String message) {
        kafkaTemplate.send("test",message + count++);
        return "success";
    }

}
