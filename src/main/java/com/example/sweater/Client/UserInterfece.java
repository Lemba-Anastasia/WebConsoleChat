package com.example.sweater.Client;

import java.io.IOException;

public interface UserInterfece extends Client{
    AgentInterface getCompanion();
    void setCompanion(AgentInterface companion);
    String getWaitingMessages();
    void clearBuffer();
    void sendMessage(String message) throws IOException;
    int getID();
    void setBufferMessages(String m);
}
