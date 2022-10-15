package com.example.distributedsharedwhiteboard.client;

import com.example.distributedsharedwhiteboard.Application.UserApplication;
import com.example.distributedsharedwhiteboard.message.JoinRequest;
import com.example.distributedsharedwhiteboard.util;
import javafx.application.Application;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * The main class for the White Board user.
 * Handles <Server Address> <Port Number> arguments
 * and starts up the White Board Apllication for the user.
 * The GUI provide interface for the user to draw shapes, and leave the application.
 *
 */
public class JoinWhiteBoard {
    static private InetAddress srvAddress;
    static private int srvPort;
    static private String username;

    static DataOutputStream output;
    static DataInputStream input;

    public static void main(String[] args) throws UnknownHostException {

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
            System.out.println("Received Wrong arguments.\n" +
                    "A default server address and port will be used");
        }

        // establish connection with Server, and launch Application if success
        try {
            Socket socket = new Socket(srvAddress, srvPort);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));

            // Send JoinWhiteBoard request to Server
            util.writeMsg(bufferedWriter,new JoinRequest(username));


            // Receive JoinWhiteBoard reply from server.

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
        // launch user application
        Application.launch(UserApplication.class, args);
    }


}
