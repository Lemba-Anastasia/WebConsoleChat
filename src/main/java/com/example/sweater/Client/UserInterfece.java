package com.example.sweater.Client;

import com.example.sweater.Client.Client;

import java.io.IOException;
import java.net.Socket;

public interface UserInterfece extends Client {
    String getWaitingMessages();
    void clearBuffer();
    boolean isWaiting();
}
