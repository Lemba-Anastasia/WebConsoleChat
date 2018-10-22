package com.example.sweater.Client.RESTClient;

import com.example.sweater.Client.AgentInterface;
import com.example.sweater.Client.UserInterfece;
import com.example.sweater.IdCounter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestUser implements UserInterfece {
    private String name;
    private AgentInterface companion;
    private String waitingPutMessages;
    private int id;
    private final List<String> restMessages;

    public RestUser(String clientName) {
        this.name = clientName;
        waitingPutMessages = "";
        id = IdCounter.getInstance().getId();
        restMessages=new ArrayList<>();
    }

    @Override
    public AgentInterface getCompanion() {
        return null;
    }

    @Override
    public void setCompanion(AgentInterface companion) {
        this.companion=companion;
    }

    @Override
    public void clearBuffer() {
        waitingPutMessages = "";
    }

    @Override
    public void sendMessage(String message) throws IOException {
        flashRESTChanel();
        companion.sendMessageToMyself(message);
        companion.setInputMessagesForREST(name + ": " + message);
    }

    @Override
    public void setBufferMessages(String m) {
        waitingPutMessages += id + "::" + name + ": " + m + "\n";
    }

    @Override
    public boolean isBusy() {
        return (companion != null);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void sendMessageToMyself(String message) throws IOException {
        setInputMessagesForREST(message);
    }

    @Override
    public void close() throws IOException {
        flashRESTChanel();
    }

    @Override
    public String getWaitingMessages() {
        return waitingPutMessages;
    }

    @Override
    public void setInputMessagesForREST(String s) {
        synchronized (restMessages){
            restMessages.add(s);
        }
    }

    @Override
    public List<String> getRESTInputMessages(){
        synchronized (restMessages) {
            return restMessages;
        }
    }

    @Override
    public void flashRESTChanel() {
        restMessages.clear();
    }

    public String toString() {
        return "WebAgent{" +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
