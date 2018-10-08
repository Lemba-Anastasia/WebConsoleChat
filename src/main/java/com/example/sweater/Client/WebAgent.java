package com.example.sweater.Client;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class WebAgent implements AgentInterface {
    private WebSocketSession socketSession;
    private String name;
    private List<UserInterfece> companionList;
    private int countOfUsers;

    public WebAgent(String clientName, WebSocketSession session, int countOfUsers) {
        name = clientName;
        socketSession = session;
        this.countOfUsers = countOfUsers;
        companionList = new ArrayList<>();
    }

    public void sendMessage(String message,int id) throws IOException {
        sendMessageToMyself(name + ": " + message);
        searchUserByID(id).sendMessageToMyself(name + ": " + message);
    }

    private Client searchUserByID(int id) {
        for (UserInterfece u : companionList ) {
            if(u.getID()==id)
                return u;
        }
        return null;
    }


    @Override
    public boolean isBusy() {
        return companionList.size() < countOfUsers;
    }


    @Override
    public void sendMessageToMyself(String message) throws IOException {
        socketSession.sendMessage(new TextMessage(message));
    }

    @Override
    public boolean hasConnectionObject(Object o) {
        return socketSession.equals(o);
    }

    @Override
    public void close() throws IOException {
        socketSession.close();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<UserInterfece> getUsers() {
        return companionList;
    }

    public int getCountOfUser() {
        return countOfUsers;
    }

    public void setCountOfUsers(int c) {
        countOfUsers = c;
    }

}
