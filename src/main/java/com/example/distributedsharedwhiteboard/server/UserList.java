package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.Logger;

import java.util.HashMap;
import java.util.Set;

public class UserList {
    private static String managerName;
    private static int managerID;

    // keep record the number of users joined, including deleted ones
    private static int counter = 0;

    Logger logger = new Logger();
    private static HashMap<Integer,String> users;

    public UserList() {
        users = new HashMap<Integer,String>();
    }

    public int addAUser (String name){
        int userId = 0;
        if (! users.containsValue(name)){
            counter++;
            users.put(counter, name);
            userId = counter;
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

    public boolean checkAUser (String userid){
        if (users.containsKey(userid)){
            return true;
        }
        return false;
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
        counter ++;
        users.put(counter, name);
        return counter;
    }

    public int getListSize(){
        return users.size();
    }

}
