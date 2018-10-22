package com.example.sweater;

import com.example.sweater.Client.*;
import com.example.sweater.consoleServer.Client.AgentConsole;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import javax.jws.soap.SOAPBinding;
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
                log.info("---------ther are " + freeUser.getName());
                synchronized (queueOfWaitingAgents) {
                    freeAgent = queueOfWaitingAgents.peekFirst();
                    if (freeAgent != null) {
                        log.info("---------ther are " + freeAgent.getName());
                        freeUser.setCompanion(freeAgent);
                        if (freeAgent instanceof WebAgent)
                            ((WebAgent) freeAgent).getUsers().add(freeUser);
                        if (freeAgent instanceof AgentConsole)
                            ((AgentConsole) freeAgent).setCompanion(freeUser);
                        log.info("---------bind " + freeAgent.getName() + " & " + freeUser.getName());
                        if (freeAgent.isBusy())
                            queueOfWaitingAgents.remove(freeAgent);
                        queueOfWaitingUsers.remove(freeUser);
                        try {
                            log.info("---------chat created between " + freeAgent.getName() + " & " + freeUser.getName());
                            freeUser.sendMessageToMyself("server: You are connected to " + freeAgent.getName());
                            freeAgent.sendMessageToMyself("NEWCHAT" + freeUser.getID());
                            freeAgent.sendMessageToMyself(freeUser.getWaitingMessages());
                            freeUser.clearBuffer();
                        } catch (IOException e) {
                            log.warning(e.getMessage());
                        }
                    }
                }
            }
        }
    }

    public void leaveCurrentChat(int idCompanion, WebAgent webAgent) {
        webAgent.getUsers().forEach(userInterfece -> {
                    if (userInterfece.getID() == idCompanion) {
                        try {
                            userInterfece.sendMessageToMyself("server: Companion left the chat");
                            userInterfece.setCompanion(null);
                            webAgent.getUsers().remove(userInterfece);
                        } catch (IOException e) {
                            log.warning(e.getMessage());
                        }
                    }
                }
        );
    }

    public void leaveChat(Client disconnectedClient) throws IOException {
        if (disconnectedClient instanceof AgentInterface) {
            if (disconnectedClient instanceof WebAgent) {
                ((WebAgent) disconnectedClient).getUsers().forEach(userInterfece -> {
                    try {
                        userInterfece.sendMessageToMyself("server: Companion left the chat");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                ((WebAgent) disconnectedClient).getUsers().forEach(userInterfece -> userInterfece.setCompanion(null));
                ((WebAgent) disconnectedClient).getUsers().clear();
            }
            if (disconnectedClient instanceof AgentConsole) {
                ((AgentConsole) disconnectedClient).getCompanion().sendMessageToMyself("server: Companion left the chat");
                ((AgentConsole) disconnectedClient).getCompanion().setCompanion(null);
                ((AgentConsole) disconnectedClient).setCompanion(null);
            }
            chatCreation();

        } else if (disconnectedClient instanceof UserInterfece) {
            AgentInterface agentInterface = ((UserInterfece) disconnectedClient).getCompanion();
            agentInterface.sendMessageToMyself(((UserInterfece) disconnectedClient).getID() + "::server: companion left the chat");
            if (((UserInterfece) disconnectedClient).getCompanion() instanceof WebAgent)
                ((WebAgent) agentInterface).getUsers().remove(disconnectedClient);
            if (((UserInterfece) disconnectedClient).getCompanion() instanceof AgentConsole)
                ((AgentConsole) agentInterface).setCompanion(null);
            ((UserInterfece) disconnectedClient).setCompanion(null);
            if (!agentInterface.isBusy()) {
                queueOfWaitingAgents.add(agentInterface);
            }
            chatCreation();
        }
    }

    public Client searchClientBySession(WebSocketSession webSession) {
        for (UserInterfece u : userList) {
            if (u instanceof WebUser) {
                if (((WebUser)u).hasConnectionObject(webSession))
                    return u;
            }
        }
        for (AgentInterface a : agentList) {
            if (a instanceof WebAgent) {
                if (((WebAgent)a).hasConnectionObject(webSession))
                    return a;
            }
        }
        return null;
    }

    public UserInterfece searchUserBySession(WebSocketSession session) {
        for (UserInterfece u : userList) {
            if (u instanceof WebUser) {
                if (((WebUser)u).hasConnectionObject(session))
                    return u;
            }
        }
        return null;
    }

    public AgentInterface searchAgentOnSession(WebSocketSession session) {
        for (AgentInterface a : agentList) {
            if (a instanceof WebAgent) {
                if (((WebAgent)a).hasConnectionObject(session))
                    return a;
            }
        }
        return null;
    }

    public List<AgentInterface> getAgentsList() {
        synchronized (agentList) {
            return agentList;
        }
    }

    public List<UserInterfece> getUsersList() {
        synchronized (userList) {
            return userList;
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

    public void exit(Object o) {
        Client client;
        if (o instanceof WebSocketSession) {
            client = searchClientBySession((WebSocketSession) o);
        } else {
            client = (Client) o;
        }
        if (client instanceof AgentInterface) {
            if (client instanceof WebAgent) {
                if (!((WebAgent) client).getUsers().isEmpty()) {
                    ((WebAgent) client).getUsers().forEach(userInterfece -> userInterfece.setCompanion(null));
                    try {
                        ((WebAgent) client).getUsers().forEach(userInterfece -> {
                            try {
                                userInterfece.sendMessageToMyself(" Companion disconnected");
                            } catch (IOException e) {
                                log.warning(e.getMessage());
                            }
                        });
                        log.info(client + " exit");
                        client.close();
                        remove(client);
                    } catch (IOException e) {
                        log.warning(e.getMessage());
                    }
                } else {
                    try {
                        client.close();
                        remove(client);
                    } catch (IOException e) {
                        log.warning(e.getMessage());
                    }
                }
            } else if (client instanceof AgentConsole) {
                if (client.isBusy()) {
                    ((AgentConsole) client).getCompanion().setCompanion(null);
                    try {
                        ((AgentConsole) client).getCompanion().sendMessageToMyself(" Companion disconnected");
                        log.info(client + " exit");
                        client.close();
                        remove(client);
                    } catch (IOException e) {
                        log.warning(e.getMessage());
                    }
                } else {
                    try {
                        client.close();
                        remove(client);
                    } catch (IOException e) {
                        log.warning(e.getMessage());
                    }
                }
            }

        } else if (client instanceof UserInterfece) {
            if (client.isBusy()) {
                queueOfWaitingAgents.add((AgentInterface) ((UserInterfece) client).getCompanion());
                chatCreation();
                log.info(client + " exit");
                try {
                    ((UserInterfece) client).getCompanion().sendMessageToMyself(((UserInterfece) client).getID() + "::server: Companion disconnected");
                    client.close();
                    remove(client);
                } catch (IOException e) {
                    log.warning(e.getMessage());
                }
            } else {
                try {
                    client.close();
                    remove(client);
                } catch (IOException e) {
                    log.warning(e.getMessage());
                }
            }
        }
    }

    public AgentInterface searchAgentByID(int id) {
        return agentList.stream().filter(agent->agent.getID()==id).findFirst().get();
    }

    public UserInterfece searchUserByID(int id) {
        return userList.stream().filter(user->user.getID()==id).findFirst().get();
    }
}
