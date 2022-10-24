package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.Logger;
import com.example.distributedsharedwhiteboard.ShapeDrawing.ShapeDrawing;
import com.example.distributedsharedwhiteboard.message.*;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.example.distributedsharedwhiteboard.Util.util.writeMsg;

public class UserList {
    private String managerName;

    private Logger logger = new Logger();

    // keep record the number of users joined, including deleted ones
    private int userListSize = 0;

    private List<String> userList;

    private HashMap<String, Socket> userSockets;

    protected UserList() {
        userList = new ArrayList<>();
        userSockets = new HashMap<>();
    }

    /**
     * Add a new user into userList
     * @param name the username provided by client
     * @return true if no repeated name in the list otherwise false
     */
    protected Boolean addAUser (String name, Socket client){
        if (! userList.contains(name)){
            userList.add(name);
            userSockets.put(name, client);
            logger.logDebug("A new User Added:"+ name);
            return true;
        } else {
            logger.logDebug("User name already exist");
            return false;
        }
    }

    public void clearUserList(){
        userList.clear();
        userSockets.clear();
    }

    /**
     * @return all names of current users
     */
    protected List<String> getAllNames() {
        return userList;
    }

    /**
     * Check is a user is currently in the userList
     * @param username
     * @return true if the user in the list otherwise false
     */
    public boolean checkAUser (String username){
        if (userList.contains(username)){
            return true;
        }
        return false;
    }

    /**
     * @param username that is leaving the white board application
     * @return true if successfully delete the user from userList otherwise false
     */
    public boolean userQuit (String username){
        if (userList.contains(username)){
            userList.remove(username);
            try {
                userSockets.get(username).close();
            } catch (IOException e) {
                logger.logWarn("Something wrong happened when closing client socket for user" + username);
                throw new RuntimeException(e);
            }
            userSockets.remove(username);
            return true;
        } else{
            logger.logDebug("User was not in UserList...");
            return false;
        }
    }


    public boolean kickOutUser (String manager, String userName){
        if (managerName.equals(manager)) {
            if (userList.contains(userName)){
                userList.remove(userName);
                userSockets.remove(userName);
                return true;
            } else{
                logger.logWarn("Delete failed: this username not exist.");
                return false;
            }
        } else {
            logger.logWarn("You are not authorized to delete users");
            return false;
        }
    }

    public Boolean setManager (String name, Socket socket){
        if (userList.size() == 0) {
            managerName = name;
            userList.add(name);
            userSockets.put(name, socket);
            return true;
        } else {
            logger.logDebug("This White board already has a manager.");
            return false;
        }
    }

    //Getters

    /**
     * @return all sockets of current users
     */
    protected Collection<Socket> getAllSockets() {
        return userSockets.values();
    }

    public Socket getManagerSocket (){
        return userSockets.get(managerName);
    }

    public Socket getUserSocketByName (String userName){
        return userSockets.get(userName);
    }

    public int getListSize(){
        return userList.size();
    }

    public String getManagerName() {
        return managerName;
    }
}
