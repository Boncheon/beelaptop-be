package com.example.sever.controler;

import com.example.sever.repository.LapTopRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
