package com.reto.catalog.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class PingController {
    @GetMapping("/ping")
    public String ping() {
        return "catalog ok";
    }

    @PostMapping("/ping")
    public String pingPost() {return "post is ok";}

    @DeleteMapping("/ping")
    public String pingDelete() {
        return "DELETE OK (ADMIN)";
    }

}
