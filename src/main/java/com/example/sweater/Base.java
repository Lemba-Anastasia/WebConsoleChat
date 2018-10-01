package com.example.sweater;

import com.example.sweater.Client.*;
import com.example.sweater.consoleServer.Client.AgentConsole;
import com.example.sweater.consoleServer.Client.UserConsole;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Configuration
public class Base {
    private final List<UserInterfece> userList;
    private final List<AgentInterface> agentList;
    private final Deque<UserInterfece> queueOfWaitingUsers;
    private final Deque<AgentInterface> queueOfWaitingAgents;
    private static final Logger log = Logger.getLogger(String.valueOf(Base.class));

    public Base() {
        userList = new ArrayList<>();
        agentList = new ArrayList<>();
        queueOfWaitingUsers = new LinkedList<>();
        queueOfWaitingAgents = new LinkedList<>();
    }

    public void addUser(UserInterfece user) {
        synchronized (userList) {
            if (userList.stream().noneMatch(user1 -> user1 == user))
                userList.add(user);
        }
    }

    public void addAgent(AgentInterface agent) {
        synchronized (agentList) {
            if (agentList.stream().noneMatch(agent1 -> agent1 == agent))
                agentList.add(agent);
        }
    }

    public void addToQWaitingAgent(AgentInterface agent) {
        synchronized (queueOfWaitingAgents) {
            if (queueOfWaitingAgents.stream().noneMatch(agent1 -> agent1 == agent)) {
                queueOfWaitingAgents.add(agent);
                agent.setCompanion(null);
            }
        }
    }

    public void addToQWaitingUsers(UserInterfece user) {
        synchronized (queueOfWaitingUsers) {
            if (queueOfWaitingUsers.stream().noneMatch(user1 -> user1 == user))
                queueOfWaitingUsers.add(user);
        }
    }

    public void remove(Client client) {
        synchronized (userList) {
            for (UserInterfece u : userList) {
                if (u == client) {
                    userList.remove(u);
                    return;
                }
            }
        }
        synchronized (agentList) {
            for (AgentInterface a : agentList) {
                if (a == client) {
                    agentList.remove(a);
                    return;
                }
            }
        }
    }

    public void chatCreation() {
        synchronized (queueOfWaitingUsers) {
            UserInterfece freeUser = queueOfWaitingUsers.peekFirst();
            if (freeUser != null) {
                AgentInterface freeAgent;
                queueOfWaitingUsers.removeFirst();
                synchronized (queueOfWaitingAgents) {
                    freeAgent = queueOfWaitingAgents.peekFirst();
                    freeUser.setCompanion(freeAgent);
                    freeAgent.setCompanion(freeUser);
                    queueOfWaitingAgents.removeFirst();
                }
                freeUser.getCompanion().setCompanion(freeUser);
                try {
                    log.info("---------send waiting mesage");
                    freeUser.sendMessageToMyself("server: You are connected to " + freeAgent.getName());
                    freeUser.getCompanion().sendMessageToMyself(freeUser.getWaitingMessages());
                    freeUser.clearBuffer();
                } catch (IOException e) {
                    log.warning(e.getMessage());
                }
            }
        }
    }

    public void leaveChat(Client client) throws IOException {//FIXME:
        if (client.getCompanion() != null) {
            try {
                client.getCompanion().sendMessageToMyself("server: Companion left the chat");
            } catch (IOException e) {
                log.warning(e.getMessage());
            }
            if (client instanceof UserInterfece) {
                queueOfWaitingAgents.add((AgentInterface) client.getCompanion());
            } else {
                queueOfWaitingAgents.add((AgentInterface) client);
            }
            client.getCompanion().setCompanion(null);
            client.setCompanion(null);
            chatCreation();
        }
    }

    public Client searchClientBySession(WebSocketSession webSession) {
        for (UserInterfece u : userList) {
            if (u.hasConnectionObject(webSession))
                return u;
        }
        for (AgentInterface a : agentList) {
            if (a.hasConnectionObject(webSession))
                return a;
        }
        return null;
    }

    public UserInterfece searchUserBySession(WebSocketSession session) {
        for (UserInterfece u : userList) {
            if (u.hasConnectionObject(session))
                return u;
        }
        return null;
    }

    public AgentInterface searchAgentOnSession(WebSocketSession session) {
        for (AgentInterface a : agentList) {
            if (a.hasConnectionObject(session))
                return a;
        }
        return null;
    }

    public void exit(Object o) {
        Client client;
        if (o instanceof WebSocketSession) {
            client = searchClientBySession((WebSocketSession) o);
        } else {
            client = (Client) o;
        }
        if (client instanceof UserInterfece) {
            queueOfWaitingAgents.add((AgentInterface) client.getCompanion());
            chatCreation();
        } else {
            client.getCompanion().setCompanion(null);
        }
        log.info(client.getName() + " left the chat");
        try {
            client.getCompanion().sendMessageToMyself(" Companion disconnected");
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
        try {
            client.close();
            remove(client);
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }

    public Deque<AgentInterface> getQueueOfWaitingAgents() {
        synchronized (queueOfWaitingAgents) {
            return queueOfWaitingAgents;
        }
    }

    public Deque<UserInterfece> getQueueOfWaitingUsers() {
        synchronized (queueOfWaitingUsers) {
            return queueOfWaitingUsers;
        }
    }

    public void removeFirstAgentFromQueue() {
        synchronized (queueOfWaitingAgents) {
            queueOfWaitingAgents.removeFirst();
        }
    }

    public void removeFirstUserFromQueue() {
        synchronized (queueOfWaitingUsers) {
            queueOfWaitingUsers.removeFirst();
        }
    }
}
