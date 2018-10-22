package com.example.sweater.Client;

import com.example.sweater.Client.RESTClient.RestUser;
import com.example.sweater.IdCounter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebAgent implements AgentInterface {
    private WebSocketSession socketSession;
    private String name;
    private List<UserInterfece> companionList;
    private int countOfUsers;
    private int id;
    private final List<String> restMessages;

    public WebAgent(String clientName, WebSocketSession session, int countOfUsers) {
        name = clientName;
        socketSession = session;
        this.countOfUsers = countOfUsers;
        companionList = new ArrayList<>();
        id = IdCounter.getInstance().getId();
        restMessages=new ArrayList<>();
    }

    public void sendMessage(String message, int id) throws IOException {
        flashRESTChanel();
        sendMessageToMyself(id + "::" + name + ": " + message);
        UserInterfece companion = searchUserByID(id);
        if (companion != null) {
            companion.sendMessageToMyself(name + ": " + message);
            if(!(companion instanceof RestUser)){
                companion.setInputMessagesForREST(name + ": " + message);
            }
        }
    }

    private UserInterfece searchUserByID(int id) {
        for (UserInterfece u : companionList) {
            if (u.getID() == id)
                return u;
        }
        return null;
    }

    @Override
    public boolean isBusy() {
        return !(companionList.size() < countOfUsers);
    }

    public boolean isHasACompanion() {
        return !companionList.isEmpty();
    }

    @Override
    public void sendMessageToMyself(String message) throws IOException {
        socketSession.sendMessage(new TextMessage(message));
    }

    public boolean hasConnectionObject(Object o) {
        return socketSession.equals(o);
    }

    @Override
    public void close() throws IOException {
        socketSession.close();
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
    public String getName() {
        return name;
    }

    public List<UserInterfece> getUsers() {
        return companionList;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String toString() {
        return "WebAgent{" +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", companionList=" + companionList +
                ", countOfMaxUsers=" + countOfUsers +
                '}';
    }
}
