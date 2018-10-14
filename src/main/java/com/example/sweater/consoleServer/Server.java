package com.example.sweater.consoleServer;

import com.example.sweater.Base;
//import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Server implements Runnable {
    private static final Logger log =Logger.getLogger(String.valueOf(Server.class));
    private List<MonoThreadClientHandler> threadClientHandlerList;
    private List<Thread> threadList;
    private ServerSocket server;
    private Socket socket;
    @Autowired
    Base base;
    public Server() {
        threadClientHandlerList = new ArrayList<>(10);
        threadList = new ArrayList<>();
        try {
            server = new ServerSocket(1357);
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
        new Thread(this).start();
    }

    public void run() {
        log.info("Waiting for client connection");
        while (!server.isClosed()) {
            try {
                socket = server.accept();
            } catch (IOException e) {
                log.warning(e.getMessage());
            }
            MonoThreadClientHandler monoThreadClientHandler = null;
            try {
                monoThreadClientHandler = new MonoThreadClientHandler(socket,
                        new BufferedReader(new InputStreamReader(socket.getInputStream())),
                        new PrintWriter(socket.getOutputStream(), true), base);
            } catch (IOException e) {
                log.warning(e.getMessage());
            }
            threadClientHandlerList.add(monoThreadClientHandler);
            log.info("Someone connected");
            Thread thead = new Thread(monoThreadClientHandler);
            thead.start();
            threadList.add(thead);
        }
    }
}
