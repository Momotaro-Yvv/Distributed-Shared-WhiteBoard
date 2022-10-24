package com.example.distributedsharedwhiteboard.client;

import com.example.distributedsharedwhiteboard.Application.ManagerApplication;
import com.example.distributedsharedwhiteboard.Application.Manager;
import com.example.distributedsharedwhiteboard.Application.User;
import com.example.distributedsharedwhiteboard.Util.JsonSerializationException;
import com.example.distributedsharedwhiteboard.message.*;
import com.example.distributedsharedwhiteboard.Util.util;
import javafx.application.Application;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import static com.example.distributedsharedwhiteboard.Util.util.readMsg;
import static com.example.distributedsharedwhiteboard.Util.util.writeMsg;

/**
 * The main class for the White Board manager.
 * Handles <Server Address> <Port Number> <username> arguments
 * and starts up the White Board Apllication for the manager.
 * The GUI provide interface for the manager to draw shapes,
 * to kick out users, and terminates the application.
 *
 */
public class CreateWhiteBoard {
    static private InetAddress srvAddress;
    static private int srvPort;
    static private String username;
    static Manager manager;
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
            writeMsg(bufferedWriter, new CreateRequest(username));

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
            if (msgFromServer.getClass().getName() == CreateReply.class.getName()) {
                CreateReply createReply = (CreateReply) msgFromServer;
                if (createReply.success){
                    manager = new Manager(username, socket);
                    String managerName = createReply.managerName;
                    manager.addUserItem(managerName);
                    System.out.println("Username: " + username);
                    System.out.println("Socket: " + socket);

                    Application.launch(ManagerApplication.class);
                }
            } else {
                ErrorMsg errorMsg = (ErrorMsg) msgFromServer;
                System.out.println(errorMsg.msg);
                System.exit(1);
            }

        } catch (IOException e) {
            System.out.println("Client received IO exception on socket.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static Manager getManager() {
        return manager;
    }
}
