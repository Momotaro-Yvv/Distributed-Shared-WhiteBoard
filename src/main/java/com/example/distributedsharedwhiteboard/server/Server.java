package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.Logger;
import com.example.distributedsharedwhiteboard.Shape.Shape;
import com.example.distributedsharedwhiteboard.Util.JsonSerializationException;
import com.example.distributedsharedwhiteboard.Util.util;
import com.example.distributedsharedwhiteboard.message.*;

import javax.net.ServerSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import static com.example.distributedsharedwhiteboard.Util.util.TransferToShape;
import static com.example.distributedsharedwhiteboard.Util.util.writeMsg;

/**
 * The main class for the White Board Server
 */
public class Server {

    private static InetAddress svrIPAddress;
    private static int svrPort;

    private static UserList userList;
    private static ObjectsList objectsList;
    private static MsgList msgList;


    static Logger svrLogger = new Logger();
    final static String welcomeMsg = " --- Welcome to Distributed Share White Board Server ---";

    public static void main(String[] args) {

        // parse command line arguments, save server ip & port
        Boolean success = parseArgs(args);

        if (success) {

            //initialize an empty user list for tracking clients
            userList = new UserList();

            ServerSocketFactory factory = ServerSocketFactory.getDefault();
            try (ServerSocket server = factory.createServerSocket(svrPort)) {

                svrLogger.logInfo("Server - [" + svrIPAddress + ":" + svrPort + "] is open,");
                svrLogger.logInfo("Waiting for client connections..");

                // Wait for connections.
                while (true) {
                    Socket client = server.accept();
                    svrLogger.logInfo("A new user has been connected");

                    // Start a new thread for each connection
                    Thread newThread = new Thread(() -> serveClient(client));
                    newThread.start();
                }

            } catch (IOException e) {
                svrLogger.logError("Failed to create Server Socket, please try again.");
                e.printStackTrace();
            }
        }
    }

    private static void serveClient(Socket client) {
        try(Socket clientSocket = client) {
            String clientIp = client.getInetAddress().getHostAddress();
            int clientPort = client.getPort();
            svrLogger.logDebug("Under new server thread, received Client request from " + clientIp + ":" + clientPort);

            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));

            // get a message
            Message msgFromClient;
            try {
                msgFromClient = util.readMsg(bufferedReader);
            } catch (JsonSerializationException e1) {
                writeMsg(bufferedWriter, new ErrorMsg("Invalid message"));
                return;
            }

            svrLogger.logDebug("MESSAGE FROM CLIENT: "+ msgFromClient);

            Boolean success;
            if (msgFromClient.getClass().getName() == CreateRequest.class.getName()){
                success = handleCreateRequest(bufferedWriter, (CreateRequest)msgFromClient, clientIp, clientPort);
            } else if (msgFromClient.getClass().getName() == JoinRequest.class.getName()) {
                success = handleJoinRequest(bufferedWriter, (JoinRequest) msgFromClient, clientIp, clientPort);
            } else {
                writeMsg(bufferedWriter,new ErrorMsg("Expecting JoinRequest or CreateRequest"));
                return;
            }


            // If the above login is successful, waiting for more messages from Client
            while(success){
                if(bufferedReader.ready()){

                    // get a message
                    Message msg;
                    try {
                        msg = util.readMsg(bufferedReader);
                    } catch (JsonSerializationException e1) {
                        util.writeMsg(bufferedWriter,new ErrorMsg("Invalid message"));
                        return;
                    };

                    // process the request message
                    handleCommand(bufferedWriter,msg);
                }
            }
        } catch (IOException | JsonSerializationException e) {
            svrLogger.logError("Encounter Json Serialization Exception. Invalid message, please try again.");
        }
    }


    /**
     * This function will check if the Command line args format is correct,
     * if correct format, set up server as input, and return true otherwise false
     * @param args Arguments from command line when run this Server
     */
    private static Boolean parseArgs(String[] args){
        if (args.length == 2){
            String serverAddress = args[0];
            String serverPort = args[1];
            try {
                Integer prt = Integer.parseInt(serverPort);
                svrPort = prt;
            } catch (NumberFormatException e) {
                svrLogger.logError("WRONG SERVER PORT NUMBER: should be an integer");
                System.exit(1);
            }
            try {
                svrIPAddress = InetAddress.getByName(serverAddress);
            }catch (UnknownHostException e) {
                svrLogger.logError("INVALID SERVER ADDRESS");
                System.exit(1);
            }
            return true;
        } else {
            svrLogger.logWarn("WRONG ARGUMENT NUMBER: Please strictly follow format <server address> <server port> ");
            System.exit(1);
        }
        return false;
    }


    /**
     * This function will deal with manager's initial Create WB request,
     * if the White Board already has a manager, then the server will send back ErrorMsg
     * Otherwise a new objects list and message History list will be initialised
     * @return true and send back CreateReply if the request successful start the WB, otherwise false
     */
    private static boolean handleCreateRequest(BufferedWriter bufferedWriter, CreateRequest msg,String ip, int port)
            throws IOException {
        //save this client as manager, and add to User list, return managerId
        svrLogger.logDebug("Client want to create a White Board");
        if (userList.getListSize() == 0) {
            String managerName = msg.username;
            Boolean success = userList.setManager(managerName);
            objectsList = new ObjectsList();
            msgList = new MsgList();
            writeMsg(bufferedWriter,new CreateReply(success));
            return true;
        } else {
            String errorMsg = "This White Board already have a manager. Please try other server port";
            util.writeMsg(bufferedWriter,new ErrorMsg(errorMsg));
            return false;
        }
    }

    /**
     * This function will deal with users' initial Join WB request,
     * If the WB hasn't had the manager then the
     * {@ handleCreateRequest} will be transparently called,
     * otherwise this user will be added to existed userList
     * @return false if failed to parse command, otherwise true and send JoinReply
     */
    private static boolean handleJoinRequest(BufferedWriter bufferedWriter,JoinRequest msg,String ip, int port)
            throws IOException {
        svrLogger.logDebug("Client want to join a White Board");
        String userName = msg.username;

        if (userList.getListSize() > 0) {
            if (!userList.checkAUser(userName)) {
                Boolean success = userList.addAUser(userName);
                svrLogger.logDebug("A new user:"+ userName + " has been added");
                writeMsg(bufferedWriter, new JoinReply(success, userList.getAllNames(), objectsList.getObjects()));
                return true;
            } else {
                writeMsg(bufferedWriter, new ErrorMsg("User name also been used. Try another one."));
                return false;
            }
        } else{
            svrLogger.logDebug("The White board does not owned by a manager yet, transparently call handleCreateRequest");
            Boolean result = handleCreateRequest(bufferedWriter, new CreateRequest(userName), ip, port);
            return result;
        }
    }


    /**
     * Handling other requests from clients
     * @return
     */
    private static void handleCommand(BufferedWriter bufferedWriter, Message message) throws JsonSerializationException, IOException {
        String command = message.getClass().getName();
        switch(command) {
            case "DrawRequest":
                DrawRequest drawRequest = (DrawRequest) message;
                String jsonShape = drawRequest.shape;
                Shape shape = TransferToShape(jsonShape);
                objectsList.addAnObject(shape);
//              util.writeMsg(); to all users in userList
                break;
            case "KickRequest":
                KickRequest kickRequest = (KickRequest) message;
                String managername = kickRequest.managerName;
                String username = kickRequest.username;
                Boolean success = userList.kickOutUser(managername, username);
//              util.writeMsg(); to all users in userList
                break;
            case "QuitMsg":
                QuitRequest message3 = (QuitRequest) message;
                String userQuitting = message3.username;
                Boolean success1 = userList.userQuit(userQuitting);
//              util.writeMsg(); to all users in userList
                break;
            case "TerminateWB":
                TerminateWB message4 = (TerminateWB) message;
//              util.writeMsg(); Goodbye to all users in userList
                break;

            default:
                try {
                    writeMsg(bufferedWriter,new ErrorMsg("Expecting a request message"));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }

    // Getter methods
    public static UserList getUserList() {
        return userList;
    }

    public static ObjectsList getObjectsList() {
        return objectsList;
    }

    public static InetAddress getSvrIPAddress() {
        return svrIPAddress;
    }

    public static int getSvrPort() {
        return svrPort;
    }

}
