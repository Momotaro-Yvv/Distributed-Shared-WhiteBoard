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

public class User {
    //properties
    private static String userName;
    int userId;
    static InetAddress srvAddress;
    static int srvPort;

    JSONObject newRequest;
    JSONObject reply;
    DataInputStream input;
    DataOutputStream output;

    bidirectionalList
    private static UserList userlist;

    private static ObjectsList objectslist;

    //Constructors
    public User(InetAddress srvAddress, int srvPort, String username){

    };

    //Getters

    //Setters

    //Methods
}
