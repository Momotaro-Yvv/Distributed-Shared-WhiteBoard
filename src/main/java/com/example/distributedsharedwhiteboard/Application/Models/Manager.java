package com.example.distributedsharedwhiteboard.Application.Models;

import com.example.distributedsharedwhiteboard.server.ObjectsList;
import com.example.distributedsharedwhiteboard.server.UserList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Manager {
    //properties
    private static String userName;
    static InetAddress srvAddress;
    static int srvPort;

    JSONObject newRequest;
    JSONObject reply;

    DataInputStream input;
    DataOutputStream output;

    private static UserList userlist;
    private static ObjectsList objectslist;

    //Constructors
    public Manager(InetAddress srvAddress, int srvPort, String username){
        try {
            this.srvAddress = srvAddress;
            this.srvPort = srvPort;
            this.userName = username;

            Socket socket = new Socket(srvAddress, srvPort);

            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            // Send JoinWhiteBoard request to Server
            newRequest = new JSONObject();
            newRequest.put("command_name", "JoinWhiteBoard");
            newRequest.put("username", userName);
            output.writeUTF(newRequest.toJSONString());
            output.flush();

            // Receive JoinWhiteBoard reply from server.
            JSONParser parser = new JSONParser();
            reply = (JSONObject) parser.parse(input.readUTF());

            /*
             * On enter the application , Log some information about the configuration settings.
             */
            //    String welcomeMsg = " --- Welcome to DS White Board ---";
            //    String connectionInfo =  "Server: " + Server.getSvrIPAddress() + ":" + Server.getSvrPort();
            //    String assignedUserId = "Your user ID: " + res.get("userid");
            // add msg to message history

        } catch (IOException e) {
            System.out.println("Client received IO exception on socket.");
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    };

    //Getters

    //Setters

    //Methods


    void sendKickUserMsg(){};
    void sendDrawMsg(){};
    void sendTerminateMsg(){};
    void sendChatMsg(){};
}
