package com.example.sweater.Client;

import java.io.IOException;
import java.util.List;

public interface Client {
    boolean isBusy();
    String getName();
    int getID();
    void sendMessageToMyself(String message) throws IOException;
    void close()throws IOException;
    void setInputMessagesForREST(String s);
    List<String> getRESTInputMessages();
    void flashRESTChanel();
}
