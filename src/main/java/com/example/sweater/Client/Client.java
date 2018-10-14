package com.example.sweater.Client;

import java.io.IOException;

public interface Client {
    boolean isBusy();
    String getName();
    void sendMessageToMyself(String message) throws IOException;
    boolean hasConnectionObject(Object o);
    void close()throws IOException;
}
