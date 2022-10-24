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
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

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

    private static LinkedBlockingDeque<Message> incomingUpdates;

    private static Logger svrLogger = new Logger();
    private final static String welcomeMsg = " --- Welcome to Distributed Share White Board Server ---";

    public static void main(String[] args) {

        // parse command line arguments, save server ip & port
        Boolean success = parseArgs(args);

        if (success) {

            //initialize an empty user list for tracking clients
            userList = new UserList();

            // initialize an empty queue to put update message
            incomingUpdates = new LinkedBlockingDeque<>();

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

                    UpdateThread updateThread = new UpdateThread(incomingUpdates, userList);
                    updateThread.start();
                }

            } catch (IOException e) {
                svrLogger.logError("Failed to create Server Socket, please try again.");
                e.printStackTrace();
            }
        }
    }

    private static void serveClient(Socket client) {
        try{

            try(Socket clientSocket = client) {
                String clientIp = client.getInetAddress().getHostAddress();
                int clientPort = client.getPort();
                svrLogger.logDebug
                        (" --------------------------  Under new server thread--------------------------------\n" +
                                "Received Client request from " + clientIp + " : " + clientPort + " " + client);

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
                    success = handleCreateRequest(bufferedWriter, (CreateRequest)msgFromClient, client);
                } else if (msgFromClient.getClass().getName() == JoinRequest.class.getName()) {
                    success = handleJoinRequest(bufferedWriter, bufferedReader, (JoinRequest) msgFromClient, client);
                } else {
                    writeMsg(bufferedWriter,new ErrorMsg("Expecting JoinRequest or CreateRequest"));
                    return;
                }


                // If the above login is successful, waiting for more messages from Client
                while(success){
                    if(bufferedReader.ready()){

                        // get a message
                        Message msg;
                        svrLogger.logDebug("------------11--------------");
                        try {
                            msg = util.readMsg(bufferedReader);
                        } catch (JsonSerializationException e1) {
                            util.writeMsg(bufferedWriter,new ErrorMsg("Invalid message"));
                            return;
                        };
                        svrLogger.logDebug("------------22--------------");
                        // process the request message
                        handleCommand(bufferedWriter,bufferedReader, msg);
                    }
                }
            } catch (IOException e) {
                svrLogger.logError("Encounter IOException");
                e.printStackTrace();
                throw new IOException(e);
            } catch (JsonSerializationException e) {
                svrLogger.logError("Encounter Json Serialization Exception.");
                throw new RuntimeException(e);
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
     * This function will deal with users' initial Join WB request
     * By sending join request to manager for approving
     */
    private static boolean handleJoinRequest
        (BufferedWriter bufferedWriter,BufferedReader bufferedReader, JoinRequest msg,Socket clientSocket) throws IOException {
        svrLogger.logDebug("Client want to join a White Board");
        String userName = msg.username;

        //Send ApproveRequest to Manager asking for approve
        try {
            svrLogger.logDebug("Manager's socket is : " + userList.getManagerSocket());
            DataOutputStream out = new DataOutputStream(userList.getManagerSocket().getOutputStream());
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
            DataInputStream in = new DataInputStream(userList.getManagerSocket().getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            writeMsg(bw, new ApproveRequest(userName));

            // need to handle manager response here
            Message readMsg = util.readMsg(br);

            if (readMsg.getClass().getName() == ApproveReply.class.getName()) {

                ApproveReply approveReply = (ApproveReply) readMsg;

                if (approveReply.approve) {
                    Boolean successAddUser = userList.addAUser(userName, clientSocket);
                    if (successAddUser) {
                        svrLogger.logDebug("A new user:" + approveReply.username + " has been added");
                        writeMsg(bufferedWriter, new JoinReply(true, userList.getAllNames(), objectsList.getObjects()));
                        incomingUpdates.add(new UpdateUserlistRequest(userName));
                        return true;
                    } else {
                        writeMsg(bufferedWriter, new ErrorMsg("User name also been used. Try another one."));
                    }
                } else {
                    writeMsg(bufferedWriter, new ErrorMsg("Manager not approve your join..."));
                }

            } else {
                writeMsg(bw, new ErrorMsg("Excepting approveReply"));
            }
            return false;
        } catch (JsonSerializationException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e){
            writeMsg(bufferedWriter, new ErrorMsg("There is no manager yet. Please try again later."));
            return false;
        }
    }


    /**
     * Handling other requests from clients
     * @return
     */
    private static void handleCommand(BufferedWriter bufferedWriter,BufferedReader bufferedReader, Message message) throws JsonSerializationException, IOException {
        String command = message.getClass().getName();
        switch(command) {
            case "com.example.distributedsharedwhiteboard.message.DrawRequest":
                DrawRequest drawRequest = (DrawRequest) message;
                String jsonShape = drawRequest.shape;
                String drawBy = drawRequest.username;

                ShapeDrawing shapeDrawing = TransferToShape(jsonShape);
                objectsList.addAnObject(shapeDrawing);
                util.writeMsg(bufferedWriter, new DrawReply(true));

                incomingUpdates.add(new UpdateShapeRequest(shapeDrawing, drawBy));
                break;
            case "com.example.distributedsharedwhiteboard.message.KickRequest":
                KickRequest kickRequest = (KickRequest) message;
                String managerName = kickRequest.managerName;
                String username = kickRequest.username;
                Boolean success = userList.kickOutUser(managerName, username);
                if (success){
                    util.writeMsg(bufferedWriter, new KickReply(true));

                    //Send ApproveRequest to Manager asking for approvement
                    DataOutputStream out = new DataOutputStream(userList.getUserSocketByName(username).getOutputStream());
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                    writeMsg(bw, new Goodbye("You have been kicked out by the manager."));

                    incomingUpdates.add(new UpdateDeleteUserRequest(username));
                } else {
                    util.writeMsg(bufferedWriter, new ErrorMsg("Can't not kick this user out"));
                }
                break;
            case "com.example.distributedsharedwhiteboard.message.QuitMsg":
                QuitRequest quitRequest = (QuitRequest) message;
                String userQuitting = quitRequest.username;
                Boolean success1 = userList.userQuit(userQuitting);
                if (success1){
                    util.writeMsg(bufferedWriter, new QuitReply(true));
                    incomingUpdates.add(new UpdateDeleteUserRequest(userQuitting));
                } else {
                    util.writeMsg(bufferedWriter,new ErrorMsg("Something went wrong when the user tries to leave"));
                }
                break;
            case "com.example.distributedsharedwhiteboard.message.TerminateWB":
                TerminateWB terminate = (TerminateWB) message;
                if (terminate.managerName == userList.getManagerName()){
                    userList.clearUserList();
                    objectsList.clearObjectList();
                    msgList.clearMsgList();
                    util.writeMsg(bufferedWriter,new Goodbye());
                } else {
                    util.writeMsg(bufferedWriter, new ErrorMsg("You are not authorised to terminate white board"));
                }

                incomingUpdates.add(new Goodbye("The manager terminate the white board"));
                break;
            case "com.example.distributedsharedwhiteboard.message.ReloadRequest":
                ReloadRequest reloadRequest = (ReloadRequest) message;
                String managerName1 = reloadRequest.managerName;
                String[] reloadShapes= reloadRequest.shapes;
                if (managerName1 == userList.getManagerName()){
                    for (String jsonObject: reloadShapes){
                        ShapeDrawing shapeDrawing1 = TransferToShape(jsonObject);
                        objectsList.addAnObject(shapeDrawing1);
                        incomingUpdates.add(new UpdateShapeRequest(shapeDrawing1, managerName1));
                    }
                    util.writeMsg(bufferedWriter,new ReloadReply(true));
                }else {
                    util.writeMsg(bufferedWriter, new ErrorMsg("Something went wrong reloading the file"));
                }
                break;
            case "com.example.distributedsharedwhiteboard.message.SendMsgRequest":
                SendMsgRequest sendMsgRequest = (SendMsgRequest) message;
                String chatMsg = sendMsgRequest.msg;
                String fromWhom = sendMsgRequest.username;
                writeMsg(bufferedWriter, new SendMsgReply(true));
                incomingUpdates.add(new UpdateMsgRequest(chatMsg, fromWhom));
                break;
            default:
                writeMsg(bufferedWriter,new ErrorMsg("handleCommand: Expecting a request message"));

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
