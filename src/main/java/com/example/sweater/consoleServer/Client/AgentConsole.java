package com.example.sweater.consoleServer.Client;

import com.example.sweater.Client.AgentInterface;
import com.example.sweater.Client.Client;
import com.example.sweater.Client.UserInterfece;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AgentConsole implements AgentInterface {
    private String name;
    private Socket socket;
    private List<UserInterfece> companionList;

    public AgentConsole(String name, Socket socket) {
        this.name = name;
        this.socket = socket;
        companionList=new ArrayList<>();
    }

    @Override
    public boolean isBusy() {
        return (!companionList.isEmpty());
    }

    public Socket getSocket() {
        return socket;
    }

    public void sendMessage(String message) throws IOException {
        UserInterfece u=companionList.get(0);
        if (u instanceof UserConsole) {
            ((UserConsole)u).getSocket().getOutputStream().write((name + ": " + message + "\n").getBytes());
            ((UserConsole)u).getSocket().getOutputStream().flush();
        }else{
            u.sendMessageToMyself(name + ": " + message);
        }
    }

    @Override
    public void sendMessageToMyself(String message) throws IOException {
        try {
            socket.getOutputStream().write((message + "\n").getBytes());
            socket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasConnectionObject(Object o) {
        return false;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<UserInterfece> getUsers() {
        return companionList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgentConsole agent = (AgentConsole) o;

        if (name != agent.getName()) return false;
        return socket == agent.getSocket();
    }
}
