package DistributedSharedWhiteboard.client;

import DistributedSharedWhiteboard.Application.User;
import DistributedSharedWhiteboard.Application.UserApplication;
import DistributedSharedWhiteboard.Util.JsonSerializationException;
import DistributedSharedWhiteboard.Util.util;
import DistributedSharedWhiteboard.message.ErrorMsg;
import DistributedSharedWhiteboard.message.JoinReply;
import DistributedSharedWhiteboard.message.JoinRequest;
import DistributedSharedWhiteboard.message.Message;
import javafx.application.Application;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * The main class for the White Board user.
 * Handles <Server Address> <Port Number> <username> arguments
 * and starts up the White Board Application for the user.
 * The GUI provide interface for the user to draw shapes, and leave the application.
 */
public class JoinWhiteBoard {
    static private InetAddress srvAddress;
    static private int srvPort;
    static private String username;
    static private User user;

    public static void main(String[] args) {

        // Parse Command Line Arguments
        if (args.length == 3) {
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
            } catch (UnknownHostException e) {
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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    new DataInputStream(socket.getInputStream()), StandardCharsets.UTF_8));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    new DataOutputStream(socket.getOutputStream()), StandardCharsets.UTF_8));

            // Send JoinWhiteBoard request to Server
            util.writeMsg(bufferedWriter, new JoinRequest(username));

            // Receive JoinWhiteBoard reply from server.
            Message msgFromServer;
            try {
                msgFromServer = util.readMsg(bufferedReader);

            } catch (JsonSerializationException e1) {
                util.writeMsg(bufferedWriter, new ErrorMsg("Invalid message"));
                return;
            }

            // launch user application
            if (msgFromServer.getClass().getName().equals(JoinReply.class.getName())) {
                JoinReply joinReply = (JoinReply) msgFromServer;
                if (joinReply.success) {
                    String[] userList = joinReply.userList;
                    String[] objectList = joinReply.objectList;
                    user = new User(username, socket);
                    user.setUserList(userList);
                    user.setObjectList(objectList);

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
