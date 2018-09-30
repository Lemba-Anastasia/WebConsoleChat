package com.example.sweater;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class GreetingController {

    @GetMapping("/agentpage")
    public String agentPage() {
        return "indexForAgent";
    }

    @GetMapping("/userpage")
    public String userPage() {
        return "indexForUser";
    }
}
