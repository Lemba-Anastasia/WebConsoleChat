package com.example.sweater.consoleServer;

import com.example.sweater.Base;
import com.example.sweater.Client.Client;
import com.example.sweater.consoleServer.Client.AgentConsole;
import com.example.sweater.consoleServer.Client.UserConsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class MonoThreadClientHandler implements Runnable {
    private static final Logger log = Logger.getLogger(String.valueOf(MonoThreadClientHandler.class));
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Client client;
    private Base base;


    public MonoThreadClientHandler(Socket socket, BufferedReader in, PrintWriter out, Base base) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.base = base;
    }

    @Override
    public void run() {
        log.info("Server reads the channel");
        try {
            String message;
            while (!socket.isClosed() && (message = in.readLine()) != null) {
                if (message.charAt(0) == '/') handlingCommandsMessage(message);
                else {
                    handlingMessage(message);
                }
            }
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }

    private void handlingMessage(String m) {
        try {
            if (client != null) {
                if (client instanceof AgentConsole)
                    onMessageReseivedFromAgent(m, (AgentConsole) client);
                else if (client instanceof UserConsole)
                    onMessageReseivedFromUser(m, (UserConsole) client);
            } else out.println("server: You are not registered");
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }

    public void onMessageReseivedFromUser(String message, UserConsole client) throws IOException {
        if (!client.isBusy()) {
            client.sendMessageToMyself("server: Waiting for a companion");
            client.setBufferMessages(message);
            base.chatCreation();
            client.sendMessage("NEWCHAT");
        } else {
            client.sendMessage(message);
            log.info("---------" + message);
        }
    }

    public void onMessageReseivedFromAgent(String message, AgentConsole client) throws IOException {
        if (client.isBusy()) {
            client.sendMessage(message);
            log.info("---------" + message);
        }
    }

    private void handlingCommandsMessage(String m) throws IOException {
        if (m.matches("/reg(\\s+)user(\\s+)\\w+")) {
            if (client != null) {
                out.println("You are aleady registered");
                return;
            }
            String clientName = m.split("/reg(\\s+)user(\\s+)")[1];
            log.info("UserNAME:" + clientName);
            UserConsole user = new UserConsole(clientName, socket);
            base.addUser(user);
            client = user;
            client.sendMessageToMyself("You have registered");
        } else if (m.matches("/reg(\\s+)agent(\\s+)\\w+")) {
            if (client != null) {
                out.println("You are aleady registered");
                return;
            }
            String clientName = m.split("/reg(\\s+)agent(\\s+)")[1];
            log.info("AgentNAME:" + clientName);
            AgentConsole agent = new AgentConsole(clientName, socket);
            base.addAgent(agent);
            client = agent;
            client.sendMessageToMyself("You have registered");
            base.addToQWaitingAgent(agent);
            base.chatCreation();
        } else if (m.matches("/leave(\\s*)")) {
            client.sendMessageToMyself("You left the chat with the previous companion");
            base.leaveChat(client);

        } else if (m.matches("/close(\\s*)")) {
            client.sendMessageToMyself("You have left");
            //TODO: base.exit(client);
        } else {
            if (client != null)
                client.sendMessageToMyself("Uncorrect command");
            else
                out.println("Uncorrect command");
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Socket getSocket() {
        return socket;
    }
}
