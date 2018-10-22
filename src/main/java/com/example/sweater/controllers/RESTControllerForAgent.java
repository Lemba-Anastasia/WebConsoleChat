package com.example.sweater.controllers;

import com.example.sweater.Base;
import com.example.sweater.Client.AgentInterface;
import com.example.sweater.Client.RESTClient.RestAgent;
import com.example.sweater.Client.WebAgent;
import com.example.sweater.Client.WebUser;
import com.example.sweater.consoleServer.Client.AgentConsole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.rmi.activation.ActivationGroup;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("rest")
public class RESTControllerForAgent {

    final private Base base;
    Logger log = Logger.getLogger(String.valueOf(RESTControllerForAgent.class));

    @Autowired
    public RESTControllerForAgent(Base base) {
        this.base = base;
    }

    @RequestMapping(value = "agents", method = RequestMethod.GET)
    public ResponseEntity<?> getAgentsList() {
        log.info("rest---getAgentsList");
        return new ResponseEntity<>(base.getAgentsList(), HttpStatus.OK);
    }

    @RequestMapping(value = "agents/waitingAgent", method = RequestMethod.GET)
    public ResponseEntity<?> getWaitingAgent() {
        log.info("rest---getWaitingAgent");
        return new ResponseEntity<>(base.getQueueOfWaitingAgents(), HttpStatus.OK);
    }

    @RequestMapping(value = "agents/{id}", method = RequestMethod.GET)//
    public ResponseEntity<?> getInformationAboutAgent(@PathVariable("id") int id) {
        log.info("rest---getInformationAboutAgent");
        try {
            AgentInterface agent = base.getAgentsList().stream()
                    .filter(agentInterface -> agentInterface.getID() == id)
                    .findFirst().get();
            return new ResponseEntity<String>((agent.toString()), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "agents/WaitingCount", method = RequestMethod.GET)
    public int getCoutOfWaitingAgents() {
        log.info("rest---getCoutOfWaitingAgents");
        return base.getQueueOfWaitingAgents().size();
    }

    @RequestMapping(value = "chats", method = RequestMethod.GET)
    public List<String> getChats() {
        log.info("rest---getChats");
        List<String> listOfChats = new ArrayList<>();
        for (AgentInterface agent : base.getAgentsList()) {
            if (agent instanceof WebAgent) {
                if (((WebAgent) agent).isHasACompanion()) {
                    listOfChats.add(agent.toString());
                }
            }
            if (agent instanceof AgentConsole) {
                if (agent.isBusy()) {
                    listOfChats.add(agent.toString());
                }
            }
        }
        return listOfChats;
    }

    @RequestMapping(value = "chats/agent/{id}", method = RequestMethod.GET)//
    public ResponseEntity<?> getInformationAboutChat(@PathVariable("id") int id) {
        log.info("rest---getInformationAboutChat");
        AgentInterface agent = base.getAgentsList().stream()
                .filter(agentInterface -> agentInterface.getID() == id)
                .findFirst().get();
        //.orElseThrow(NoSuchElementException::new);
        return new ResponseEntity<>(agent.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "agents", method = RequestMethod.POST)
    public ResponseEntity<?> registationNewAgent(@RequestBody Map map) {//
        log.info("rest/agents/ POST data " + map);
        RestAgent restAgent = new RestAgent((String) (map.get("name")), (int) map.get("coutOfUsers"));
        base.addAgent(restAgent);
        base.addToQWaitingAgent(restAgent);
        base.chatCreation();
        return new ResponseEntity<>(restAgent.getID(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "agents/{id}/chat", method = RequestMethod.POST)
    public ResponseEntity<?> sendMessageFromAgent(@RequestBody Map map, @PathVariable("id") int id) {
        log.info("rest/agents/{ID}/ POST mesage: " + map.get("message"));
        String message = (String) map.get("message");
        AgentInterface agent = base.searchAgentByID(id);
        if (agent != null) {
            try {
                if (agent instanceof WebAgent) {
                    ((WebAgent) agent).sendMessage(message, (int) map.get("idOfCompanion"));
                } else if (agent instanceof AgentConsole) {
                    ((AgentConsole) agent).sendMessage(message);
                } else if (agent instanceof RestAgent) {
                    ((RestAgent) agent).sendMessage(message, (int) map.get("idOfCompanion"));
                }
            } catch (IOException e) {
                log.warning("error to sendMessage to Agent");
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "agents/{id}/newMessages", method = RequestMethod.GET)
    public ResponseEntity<?> getMessageFromAgent(@PathVariable("id") int id) {
        log.info("rest/agents/"+id+"/newMessages GET");
        AgentInterface agent = base.searchAgentByID(id);
        return new ResponseEntity<>(agent.getRESTInputMessages(), HttpStatus.OK);
    }

    @RequestMapping (value = "agents/{ig}/leaveChat",method = RequestMethod.POST)
    public ResponseEntity<?> leaveAgentFromChat(@PathVariable("id") int id){
        log.info("rest/agents/"+id+"/leaveChat POST");
        AgentInterface agent = base.searchAgentByID(id);
        try {
            base.leaveChat(agent);
            agent.close();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            log.warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
