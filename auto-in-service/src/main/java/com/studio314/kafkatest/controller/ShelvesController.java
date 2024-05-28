package com.studio314.kafkatest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shelves")
public class ShelvesController {

    @PostMapping
    public String addShelves() {
        return "Shelves added";
    }

}
