package com.example.sweater.Client.RESTClient;

import com.example.sweater.Client.AgentInterface;
import com.example.sweater.Client.UserInterfece;
import com.example.sweater.IdCounter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestAgent implements AgentInterface {
    private String name;
    private List<UserInterfece> companionList;
    private int countOfUsers;
    private int id;
    private final List<String> restMessages;
    public RestAgent(String name, int countOfUsers){
        this.name = name;
        this.countOfUsers = countOfUsers;
        companionList = new ArrayList<>();
        id = IdCounter.getInstance().getId();
        restMessages=new ArrayList<>();

    }
    @Override
    public boolean isBusy() {
        return !(companionList.size() < countOfUsers);
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

    @Override
    public String toString() {
        return "RestAgent{" +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", companionList=" + companionList +
                ", countOfMaxUsers=" + countOfUsers +
                '}';
    }

    public void sendMessage(String message, int idOfCompanion) throws IOException {
        flashRESTChanel();
        UserInterfece companion = searchUserByID(idOfCompanion);
        if (companion != null){
            companion.sendMessageToMyself(name + ": " + message);
            companion.setInputMessagesForREST(name + ": " + message);
        }
    }

    private UserInterfece searchUserByID(int id) {
        return companionList.stream().filter(companion->companion.getID()==id).findFirst().get();
    }
}
