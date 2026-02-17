package com.drycleaning.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/orders/new")
    public String newOrder() {
        return "new-order";
    }

    @GetMapping("/orders/search")
    public String searchOrder() {
        return "search-order";
    }

    @GetMapping("/customers")
    public String customers() {
        return "customers";
    }

    @GetMapping("/statistics")
    public String statistics() {
        return "statistics";
    }
}