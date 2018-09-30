package com.example.sweater;

import com.example.sweater.consoleServer.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        new Thread(new Server()).start();
    }
}
