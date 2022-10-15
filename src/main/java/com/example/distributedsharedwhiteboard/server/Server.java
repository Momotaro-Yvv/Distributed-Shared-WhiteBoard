package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.Logger;
import com.example.distributedsharedwhiteboard.Shape.Shape;
import com.example.distributedsharedwhiteboard.message.*;
import com.example.distributedsharedwhiteboard.util;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.net.ServerSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import static com.example.distributedsharedwhiteboard.util.writeMsg;

/**
 * The main class for the White Board Server
 */
public class Server {

    private static UserList userList;
    private static ObjectsList objectsList;
    private static InetAddress svrIPAddress;
    private static int svrPort;
    static Logger svrLogger = new Logger();
    final static String welcomeMsg = " --- Welcome to Distributed Share White Board Server ---";

    public static void main(String[] args) {

        // parse command line arguments, save server ip & port
        Boolean success = parseArgs(args);

        if (success) {

            //initialize an empty user list for tracking clients
            userList = new UserList();
            objectsList = new ObjectsList();

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

    private static void serveClient(Socket client) throws IOException {
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

            Boolean success = false;
            if (msgFromClient.getClass().getName() == CreateReply.class.getName()){
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
                    String msgname = msg.getClass().getName();
                    handleCommand(bufferedWriter,msgname);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function will check if the args format is correct,
     * if correct, set input as server port number, and return True
     * otherwise, use default setting 127.0.0.1: 3000
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
    }

    /**
     * This function will deal with manager' initial login
     * @return false if failed to parse command, otherwise true and send CreateReply
     */
    private static boolean handleCreateRequest(BufferedWriter bufferedWriter, CreateRequest msg,String ip, int port)
            throws IOException {
        //save this client as manager, and add to User list, return managerId
        svrLogger.logDebug("Client"+ ip + port +"want to create a White Board");
        if (userList.getListSize() == 0) {
            String managerName = msg.username;
            int managerId = userList.addManager(managerName);
            writeMsg(bufferedWriter,new CreateReply(managerId));
            return true;
        } else {
            svrLogger.logError("This White Board already have a manager. Please try other server port");
            return false;
        }
    }

    /**
     * This function will deal with users' initial login
     * @return false if failed to parse command, otherwise true and send JoinReply
     */
    private static boolean handleJoinRequest(BufferedWriter bufferedWriter,JoinRequest msg,String ip, int port)
            throws IOException {
        svrLogger.logDebug("Client want to join a White Board");
        String userName = msg.username;

        if (userList.getListSize() > 0) {
            if (!userList.checkAUser(userName)) {
                int userId = userList.addAUser(userName);
                svrLogger.logDebug("A new user: ID:  " + userId + " Username " + userName + " has been added");
                writeMsg(bufferedWriter, new JoinReply(userId));
                return true;
            } else {
                svrLogger.logDebug("The White board does not owned by a manager yet, transparently call handleCreateRequest");
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
     * @param commands
     * @return
     */
    private static void handleCommand(BufferedWriter bufferedWriter, String command){

        switch(command) {
            case DrawRequest.class.getName():
                Shape x = (Shape) commands.get("shape");
                objectsList.addAnObject(x);
                break;
            case KickRequest.class.getName():
                int managerId = (int) commands.get("managerid");
                int userid = (int) commands.get("kickwho");
                userList.deleteAUser(managerId, userid);
                break;
            case QuitMsg.class.getName():
                Shape x1 = (Shape) commands.get("shape");
                objectsList.deleteAnObject(x1);
                break;
            case TerminateWB.class.getName():
                Shape x1 = (Shape) commands.get("shape");
                objectsList.deleteAnObject(x1);
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

    public static String getSvrIPAddress() {
        return svrIPAddress;
    }

    public static int getSvrPort() {
        return svrPort;
    }

}
