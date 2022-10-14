package com.example.distributedsharedwhiteboard.client;

import com.example.distributedsharedwhiteboard.Application.ManagerApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The main class for the White Board manager.
 * Handles <Server Address> <Port Number> arguments
 * and starts up the White Board Apllication for the manager.
 * The GUI provide interface for the manager to draw shapes,
 * to kick out users, and terminates the application.
 *
 */
public class CreateWhiteBoard {


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
                srvAddress = InetAddress.getByName(serverAddress);
            } catch (NumberFormatException e) {
                System.out.println("Server port number should be an integer");
                System.exit(1);
            }

            String[] arguments = new String[] {srvAddress.toString(), String.valueOf(srvPort)};
            ManagerApplication.main(arguments);
        } else {
            System.out.println("Received Wrong arguments.\n" +
                    "A default server address and port will be used");
        }

    }


}
