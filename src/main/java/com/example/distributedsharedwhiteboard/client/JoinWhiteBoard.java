package com.example.distributedsharedwhiteboard.client;

import com.example.distributedsharedwhiteboard.Application.UserApplication;

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

    public static void main(String[] args) throws UnknownHostException {

        // Default settings
        InetAddress localAddress = InetAddress.getByName("localhost");
        int localPort = 3201;
        InetAddress srvAddress = InetAddress.getByName("localhost");
        int srvPort = 3000;


        // Parse Command Line Arguments
        if (args.length == 2){
            String serverAddress = args[0];
            String serverPort = args[1];
            try {
                srvPort = Integer.parseInt(serverPort);
            } catch (NumberFormatException e) {
                System.out.println("Server port number should be an integer");
                System.exit(1);
            }
            srvAddress = InetAddress.getByName(serverAddress);
            String[] arguments = new String[] {srvAddress.toString(), String.valueOf(srvPort)};

            UserApplication.main(arguments);
        } else {
            System.out.println("Received Wrong arguments.\n" +
                    "A default server address and port will be used");
        }

    }

}
