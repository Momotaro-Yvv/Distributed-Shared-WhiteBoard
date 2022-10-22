package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class UserList {
    private String managerName;

    private Logger logger = new Logger();

    // keep record the number of users joined, including deleted ones
    private int userListSize = 0;

    private List<String> userList;

    protected UserList() {
        userList = new ArrayList<>();
    }

    /**
     * Add a new user into userList
     * @param name the username provided by client
     * @return true if no repeated name in the list otherwise false
     */
    protected Boolean addAUser (String name){
        if (! userList.contains(name)){
            userList.add(name);
            logger.logDebug("New User Added:"+ name);
            return true;
        } else {
            logger.logDebug("User name already exist");
            return false;
        }
    }

    public void clearUserList(){
        userList.clear();
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
            return true;
        } else{
            logger.logDebug("User was not in UserList...");
            return false;
        }
    }


    public boolean kickOutUser (String manager, String userName){
        if (managerName == manager) {
            if (userList.contains(userName)){
                userList.remove(userName);
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

    public Boolean setManager (String name){
        if (userList.size() == 0) {
            managerName = name;
            userList.add(name);
            return true;
        } else {
            logger.logDebug("This White board already has a manager.");
            return false;
        }

    }

    public int getListSize(){
        return userList.size();
    }

}
