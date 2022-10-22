package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.Logger;
import com.example.distributedsharedwhiteboard.ShapeDrawing.ShapeDrawing;
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

    private static int timeout = 2000;
    private static UserList userList;
    private static ObjectsList objectsList;
    private static MsgList msgList;

    private static Logger svrLogger = new Logger();
    private final static String welcomeMsg = " --- Welcome to Distributed Share White Board Server ---";

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
        try{
            client.setSoTimeout(timeout);

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

                Boolean success;
                if (msgFromClient.getClass().getName() == CreateRequest.class.getName()){
                    success = handleCreateRequest(bufferedWriter, (CreateRequest)msgFromClient, clientSocket);
                } else if (msgFromClient.getClass().getName() == JoinRequest.class.getName()) {
                    success = handleJoinRequest(bufferedWriter, bufferedReader, (JoinRequest) msgFromClient, clientSocket);
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

        } catch(IOException e){
            svrLogger.logWarn("Something went wrong with the connection");
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
    private static boolean handleCreateRequest(BufferedWriter bufferedWriter, CreateRequest msg, Socket clientSocket)
            throws IOException {
        //save this client as manager, and add to User list, return managerId
        svrLogger.logDebug("Client want to create a White Board...");
        String managerName = msg.username;
        Boolean success = userList.setManager(managerName, clientSocket);
        if (success) {
            objectsList = new ObjectsList();
            msgList = new MsgList();
            writeMsg(bufferedWriter,new CreateReply(true));
            return true;
        } else {
            String errorMsg = "This White Board already have a manager. Please try other server port";
            util.writeMsg(bufferedWriter,new ErrorMsg(errorMsg));
            return false;
        }
    }

    /**
     * This function will deal with users' initial Join WB request,
     * If the WB hasn't had the manager then the handleCreateRequest will be transparently called,
     * otherwise this user will be added to existed userList
     * @return false if failed to parse command, otherwise true and send JoinReply
     */
    private static boolean handleJoinRequest(BufferedWriter bufferedWriter,BufferedReader bufferedReader, JoinRequest msg,Socket clientSocket)
            throws IOException {
        svrLogger.logDebug("Client want to join a White Board");
        String userName = msg.username;

        //Send ApproveRequest to Manager asking for approvement
        DataOutputStream out = new DataOutputStream(userList.getManagerSocket().getOutputStream());
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        writeMsg(bw, new ApproveRequest(userName));

        // get a message
        Message msgFromManager;
        try {
            msgFromManager = util.readMsg(bufferedReader);
        } catch (JsonSerializationException e1) {
            writeMsg(bufferedWriter, new ErrorMsg("Invalid message"));
            return false;
        }

        Boolean approve = false;
        if (msgFromManager.getClass().getName() == ApproveReply.class.getName()){
            ApproveReply approveReply = (ApproveReply)msgFromManager;
            approve = approveReply.approve;
        }
        if (approve){
            if (userList.getListSize() > 0) {
                Boolean success = userList.addAUser(userName,clientSocket);
                if (success) {
                    svrLogger.logDebug("A new user:"+ userName + " has been added");
                    writeMsg(bufferedWriter, new JoinReply(true, userList.getAllNames(), objectsList.getObjects()));
                    return true;
                } else {
                    writeMsg(bufferedWriter, new ErrorMsg("User name also been used. Try another one."));
                    return false;
                }
            } else{
                String error = "The White board does not owned by a manager yet, You can create a White board instead";
                writeMsg(bufferedWriter, new ErrorMsg(error));
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * Handling other requests from clients
     * @return
     */
    private static void handleCommand(BufferedWriter bufferedWriter, Message message) throws JsonSerializationException, IOException {
        String command = message.getClass().getName();
        switch(command) {
            case "com.example.distributedsharedwhiteboard.message.DrawRequest":
                DrawRequest drawRequest = (DrawRequest) message;
                String jsonShape = drawRequest.shape;
                String drawBy = drawRequest.username;

                ShapeDrawing shapeDrawing = TransferToShape(jsonShape);
                objectsList.addAnObject(shapeDrawing);
                util.writeMsg(bufferedWriter, new DrawReply());
                break;
            case "com.example.distributedsharedwhiteboard.message.KickRequest":
                KickRequest kickRequest = (KickRequest) message;
                String managerName = kickRequest.managerName;
                String username = kickRequest.username;
                Boolean success = userList.kickOutUser(managerName, username);
                if (success){
                    util.writeMsg(bufferedWriter, new KickReply(true));
                }
                break;
            case "com.example.distributedsharedwhiteboard.message.QuitMsg":
                QuitRequest quitRequest = (QuitRequest) message;
                String userQuitting = quitRequest.username;
                Boolean success1 = userList.userQuit(userQuitting);
                if (success1){
                    util.writeMsg(bufferedWriter, new QuitReply(true));
                }
                break;
            case "com.example.distributedsharedwhiteboard.message.TerminateWB":
                TerminateWB terminate = (TerminateWB) message;
                userList.clearUserList();
                objectsList.clearObjectList();
                msgList.clearMsgList();
                util.writeMsg(bufferedWriter,new Goodbye());
                break;

            default:
                writeMsg(bufferedWriter,new ErrorMsg("Expecting a request message"));

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
