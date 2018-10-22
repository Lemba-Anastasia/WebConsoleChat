package com.example.sweater.controllers;

import com.example.sweater.Base;
import com.example.sweater.Client.RESTClient.RestUser;
import com.example.sweater.Client.UserInterfece;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@RestController
@RequestMapping("rest")
public class RESTControllerForUser {
    final private Base base;
    Logger log = Logger.getLogger(String.valueOf(RESTControllerForUser.class));

    @Autowired
    public RESTControllerForUser(Base base) {
        this.base = base;
    }

    @RequestMapping(value = "user/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getInformationAboutUser(@PathVariable("id") int id) {
        log.info("rest/user/"+id+" GET");
        try {
            UserInterfece user = base.getUsersList().stream()
                    .filter(agentInterface -> agentInterface.getID() == id)
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new);
            return new ResponseEntity<>((user), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "user/WaitingCount", method = RequestMethod.GET)
    public int getCoutOfWaitingUsers() {
        log.info("rest/user/WaitingCount GET");
        return base.getQueueOfWaitingUsers().size();
    }

    @RequestMapping(value = "users", method = RequestMethod.POST)
    public ResponseEntity<?> registationNewAgent(@RequestBody Map map) {///
        log.info("rest/users/ POST data "+map+" POST");
        RestUser restUser=new RestUser((String) (map.get("name")));
        base.addUser(restUser);
        return new ResponseEntity<>(restUser.getID(),HttpStatus.CREATED);
    }

    @RequestMapping(value = "users/{id}/chat", method = RequestMethod.POST)
    public ResponseEntity<?> sendMessageFromUser(@RequestBody Map map, @PathVariable("id") int id) {
        String message = (String) map.get("message");
        log.info("rest/users/"+id+"/ POST mesage: " + message);
        UserInterfece  user= base.searchUserByID(id);
        if (user != null) {
            try {
                if (!user.isBusy()) {
                    base.addToQWaitingUsers(user);
                    user.sendMessageToMyself("server: Waiting for a companion");
                    user.setBufferMessages(message);
                    base.chatCreation();
                    log.info("---------" + message + " from user " + user.getName());
                } else {
                    user.sendMessage(user.getID() + "::" + user.getName() + ": " + message);
                    log.info("---------" + message);
                }
            } catch (IOException e) {
                log.warning("error to sendMessage to Agent");
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "users/{id}/newMessages", method = RequestMethod.GET)
    public ResponseEntity<?> getMessageFromUser(@PathVariable("id") int id) {
        log.info("rest/users/"+id+"/newMessages GET");
        UserInterfece user = base.searchUserByID(id);
        return new ResponseEntity<>(user.getRESTInputMessages(), HttpStatus.OK);
    }

    @RequestMapping (value = "users/{ig}/leaveChat",method = RequestMethod.POST)
    public ResponseEntity<?> leaveAgentFromChat(@PathVariable("id") int id){
        log.info("rest/users/"+id+"/leaveChat POST");
        UserInterfece user= base.searchUserByID(id);
        try {
            user.close();
            base.leaveChat(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            log.warning(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
