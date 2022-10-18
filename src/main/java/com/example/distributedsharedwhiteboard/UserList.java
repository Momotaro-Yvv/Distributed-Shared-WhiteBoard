package com.example.distributedsharedwhiteboard;

import java.util.HashMap;
import java.util.Set;

public class UserList {
    private String managerName;
    private int managerID;

    private Logger logger = new Logger();

    // keep record the number of users joined, including deleted ones
    private int userListSize = 0;

    private HashMap<Integer,String> users;

    // TODO: remove user id, only use username
    public UserList() {
        users = new HashMap<Integer,String>();
    }

    public int addAUser (String name){
        int userId = 0;
        if (! users.containsValue(name)){
            userListSize++;
            users.put(userListSize, name);
            userId = userListSize;
        } else {
            logger.logDebug("User name already exist");
        }
        return userId;
    }

    public Set<Integer> getAllIds() {
        return users.keySet();
    }

    public Set<String> getAllNames() {
        return (Set<String>) users.values();
    }

    public boolean checkAUser (String username){
        if (users.containsValue(username)){
            return true;
        }
        return false;
    }

    public boolean userQuit (Integer userId){
        if (users.containsKey(userId)){
            users.remove(userId);
            return true;
        } else{
            logger.logDebug("User was not in UserList anyway...");
            return false;
        }
    }

    public boolean deleteAUser (int managerId, int userid){
        Boolean success = false;
        if (managerId == managerID) {
            if (users.containsKey(userid)){
                users.remove(userid);
                success = true;
            } else{
                logger.logWarn("Delete failed: this user not exist.");
            }
        } else {
            logger.logWarn("You are not authorized to delete users");
        }
       return success;
    }

    public int addManager (String name){
        managerName = name;
        userListSize++;
        users.put(userListSize, name);
        return userListSize;
    }

    public int getListSize(){
        return users.size();
    }

}
