package com.example.distributedsharedwhiteboard.client;

import com.example.distributedsharedwhiteboard.Application.UserApplication;

import javafx.application.Application;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The main class for the White Board user.
 * Handles <Server Address> <Port Number> arguments
 * and starts up the White Board Apllication for the user.
 * The GUI provide interface for the user to draw shapes, and leave the application.
 *
 */
public class JoinWhiteBoard {

    private static String userName;
    private static InetAddress srvAddress;
    private static int srvPort;

    public static String getUserName() {
        return userName;
    }

    public static InetAddress getSrvAddress() {
        return srvAddress;
    }

    public static int getSrvPort() {
        return srvPort;
    }

    public static void main(String[] args) throws UnknownHostException {
        // Parse Command Line Arguments
        if (args.length == 3){
            String arg1 = args[0];
            String arg2 = args[1];
            String arg3 = args[2];
            try {
                srvPort = Integer.parseInt(arg2);
            } catch (NumberFormatException e) {
                System.out.println("WRONG FORMAT: Server port number should be an integer");
                System.exit(1);
            }
            try {
                srvAddress = InetAddress.getByName(arg1);
            } catch (UnknownHostException e){
                System.out.println("No IP address for the host could be found");
                System.exit(1);
            }
            userName = arg3;


        } else {
            System.out.println("Received Wrong arguments.\n" +
                    "A default server address and port will be used");
        }

        // launch user application
        Application.launch(UserApplication.class, args);
    }


}
