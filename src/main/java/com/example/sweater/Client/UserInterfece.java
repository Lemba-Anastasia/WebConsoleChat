package com.example.sweater.Client;

import com.example.sweater.Client.Client;

import java.io.IOException;
import java.net.Socket;

public interface UserInterfece extends Client {
    AgentInterface getCompanion();
    void setCompanion(AgentInterface companion);
    String getWaitingMessages();
    void clearBuffer();
    boolean isWaiting();
    void sendMessage(String message) throws IOException;
    int getID();
    void setBufferMessages(String m);
}
