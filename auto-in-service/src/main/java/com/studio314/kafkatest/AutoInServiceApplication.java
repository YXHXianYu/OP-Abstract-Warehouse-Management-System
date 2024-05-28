package com.studio314.kafkatest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class AutoInServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoInServiceApplication.class, args);
    }

}
