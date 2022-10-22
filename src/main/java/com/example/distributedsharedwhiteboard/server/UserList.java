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
     * @return all sockets of current users
     */
    protected Collection<Socket> getAllSockets() {
        return userSockets.values();
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
        if (managerName == manager) {
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

    public Socket getManagerSocket (){
        return userSockets.get(managerName);
    }


    public int getListSize(){
        return userList.size();
    }

    /**
     * Send a UpdateUserlistRequest to all other users about the update except sender
     * @param newUser who just joined in the white board
     * @throws IOException
     */
    private void sendNewUserToAllUsers(String newUser) throws IOException {
        for (Socket other : userSockets.values()) {
            if (other == userSockets.get(newUser)) {
                continue;//ignore the sender client.
            }
            DataOutputStream out = new DataOutputStream(other.getOutputStream());
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
            writeMsg(bw, new UpdateUserlistRequest(newUser));
        }
    }

    /**
     * Send a UpdateShapeRequest to all other users about the update except sender
     * @param fromWhom is user who drawn new shapes on the white board
     * @throws IOException
     */
    private void sendNewShapeToAllUsers(String fromWhom, ShapeDrawing newShape) throws IOException {
        for (Socket other : userSockets.values()) {
            if (other == userSockets.get(fromWhom)) {
                continue;//ignore the sender client.
            }
            DataOutputStream out = new DataOutputStream(other.getOutputStream());
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
            writeMsg(bw, new UpdateShapeRequest(newShape));
        }
    }

    /**
     * Send a UpdateMsgRequest to all other users about the update except sender
     * @param fromWhom is user who sent new messgae onto chat box
     * @throws IOException
     */
    private void sendChatToAllUsers(String fromWhom, String msg) throws IOException {
        for (Socket other : userSockets.values()) {
            if (other == userSockets.get(fromWhom)) {
                continue;//ignore the sender client.
            }
            DataOutputStream out = new DataOutputStream(other.getOutputStream());
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
            writeMsg(bw, new UpdateMsgRequest(msg));
        }
    }

}
