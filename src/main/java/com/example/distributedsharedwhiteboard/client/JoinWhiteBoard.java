package com.example.distributedsharedwhiteboard.client;

import com.example.distributedsharedwhiteboard.Application.ManagerApplication;
import com.example.distributedsharedwhiteboard.Application.Manager;
import com.example.distributedsharedwhiteboard.Application.User;
import com.example.distributedsharedwhiteboard.Application.UserApplication;
import com.example.distributedsharedwhiteboard.ShapeDrawing.ShapeDrawing;
import com.example.distributedsharedwhiteboard.Util.JsonSerializationException;
import com.example.distributedsharedwhiteboard.message.*;
import com.example.distributedsharedwhiteboard.Util.util;
import javafx.application.Application;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.distributedsharedwhiteboard.Util.util.*;

/**
 * The main class for the White Board user.
 * Handles <Server Address> <Port Number> <username> arguments
 * and starts up the White Board Apllication for the user.
 * The GUI provide interface for the user to draw shapes, and leave the application.
 *
 */
public class JoinWhiteBoard {
    static private InetAddress srvAddress;
    static private int srvPort;
    static private String username;
    static private User user;
    static DataOutputStream output;
    static DataInputStream input;

    public static void main(String[] args) {

        // Parse Command Line Arguments
        if (args.length == 3){
            String arg1 = args[0];
            String arg2 = args[1];
            String arg3 = args[2];
            try {
                srvPort = Integer.parseInt(arg2);
            } catch (NumberFormatException e) {
                System.out.println("WRONG SERVER PORT NUMBER: should be an integer");
                System.exit(1);
            }
            try {
                srvAddress = InetAddress.getByName(arg1);
            } catch (UnknownHostException e){
                System.out.println("WRONG SERVER ADDRESS: No IP address for this server host could be found");
                System.exit(1);
            }
            username = arg3;
        } else {
            System.out.println("Received Wrong arguments.\n");
            System.exit(1);
        }

        // establish connection with Server, and launch Application if success
        try {
            Socket socket = new Socket(srvAddress, srvPort);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));

            // Send JoinWhiteBoard request to Server
            writeMsg(bufferedWriter, new JoinRequest(username));

            // Receive JoinWhiteBoard reply from server.
            Message msgFromServer;
            try {
                msgFromServer = readMsg(bufferedReader);
                System.out.println("received: "+ msgFromServer.toString());
            } catch (JsonSerializationException e1) {
                writeMsg(bufferedWriter, new ErrorMsg("Invalid message"));
                return;
            }

            // launch user application
            if (msgFromServer.getClass().getName() == JoinReply.class.getName()){
                JoinReply joinReply = (JoinReply)msgFromServer;
                if (joinReply.success){
                    String[] userList = joinReply.userList;
                    String[] objectList =joinReply.objectList;
                    user = new User(username, socket);
                    user.setUserList(userList);

                    user.setUserList(userList);
                    user.setObjectList(objectList);

                    System.out.println("Username: " + username);
                    System.out.println("Socket: " + socket);

                    Application.launch(UserApplication.class);
                }
            } else {
                ErrorMsg errorMsg = (ErrorMsg) msgFromServer;
                System.out.println(errorMsg.msg);
                System.exit(1);
            }

        } catch (IOException e) {
            System.out.println("Client received IO exception on socket.");
            throw new RuntimeException(e);
        } catch (JsonSerializationException e) {
            System.out.println("Something wrong with adding shapes into user objectList");
            throw new RuntimeException(e);
        }
    }

    public static User getUser() {
        return user;
    }
}
