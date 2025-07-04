package com.example.sever.controller;

import com.example.sever.repository.LapTopRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class lapTopcontroller {
    LapTopRepository lapToprepository;

    @GetMapping("/laptop")
    public String getLaptop() {
        return "Hello World";
    }
}
