package com.example.sweater.consoleServer.Client;

import com.example.sweater.Client.AgentInterface;
import com.example.sweater.Client.RESTClient.RestAgent;
import com.example.sweater.Client.UserInterfece;
import com.example.sweater.IdCounter;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UserConsole implements UserInterfece {
    private String name;
    private Socket socket;
    private AgentInterface companion;
    private String waitingPutMessages;
    private int id;
    final private List<String> restMessages;
    public UserConsole(String name, Socket socket) {
        this.name=name;
        this.socket=socket;
        companion=null;
        waitingPutMessages="";
        id= IdCounter.getInstance().getId();
        restMessages=new ArrayList<>();
    }

    @Override
    public void sendMessage(String message) throws IOException {
        flashRESTChanel();
        if (companion instanceof AgentConsole) {
            ((AgentConsole)companion).getSocket().getOutputStream().write((message + "\n").getBytes());
            ((AgentConsole)companion).getSocket().getOutputStream().flush();
        }else{
            companion.sendMessageToMyself(message);
            if(!(companion instanceof RestAgent)){
                companion.setInputMessagesForREST(name + ": " + message);
            }
        }
    }

    @Override public void sendMessageToMyself(String message) throws IOException  {
        socket.getOutputStream().write((message+"\n").getBytes());
        socket.getOutputStream().flush();
    }

    @Override
    public int getID(){
        return id;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public boolean isBusy() {
        return (companion!=null);
    }

    @Override
    public void setCompanion(AgentInterface companion) {
        this.companion= companion;
    }

    public Socket getSocket() {
        return socket;
    }
    @Override
    public AgentInterface getCompanion(){
        return companion;
    }


    @Override
    public String getName(){
        return name;
    }

    @Override
    public void setBufferMessages(String m){
        waitingPutMessages+=id + "::"+name + ": " + m + "\n";
    }

    @Override
    public void clearBuffer(){waitingPutMessages="";}

    @Override
    public String getWaitingMessages() {
        return waitingPutMessages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserConsole user = (UserConsole) o;

        if (!name.equals(user.getName())) return false;
        return socket == user.getSocket();
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
        return "UserConsole{" +
                ", name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
