package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.Logger;
import com.example.distributedsharedwhiteboard.Shape.Shape;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ServerSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The main class for the White Board Server
 */
public class Server {

    private static UserList userList;
    private static ObjectsList objectsList;
    private static String svrIPAddress;
    private static int svrPort;
    static Logger svrLogger = new Logger();
    final static String welcomeMsg = " --- Welcome to Distributed Share White Board Server ---";

    public static void main(String[] args) {
        // parse command line arguments, save server ip & port
        // DEFAULT Server: 127.0.0.1: 3000
        parseArgs(args);

        //initialize an empty user list for tracking clients
        userList = new UserList();
        objectsList = new ObjectsList();

        ServerSocketFactory factory = ServerSocketFactory.getDefault();
        try(ServerSocket server = factory.createServerSocket(svrPort)){

            svrLogger.logInfo("Server - ["+svrIPAddress+":"+svrPort+"] is open,");
            svrLogger.logInfo("Waiting for client connection..");

            // Wait for connections.
            while(true){
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

    private static void serveClient(Socket client)
    {
        try(Socket clientSocket = client)
        {
            String clientIp = client.getInetAddress().getHostAddress();
            svrLogger.logDebug("Under new server thread, now processing Client request on connection " + clientIp);

            JSONParser parser = new JSONParser();
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

            String clientMsg = input.readUTF();
            svrLogger.logDebug("MESSAGE FROM CLIENT: "+ clientMsg );
            JSONObject command = (JSONObject) parser.parse(clientMsg);

            Integer client_id = parseLoginCommand(command);
            output.writeUTF(welcomeMsg + "\n Assigned client id: " + client_id);
            JSONObject newCommand = new JSONObject();
            newCommand.put("userid", client_id);
            // Send message to Client
            output.writeUTF(newCommand.toJSONString());
            output.flush();

            // Waiting for more message from Client
            while(true){
                if(input.available() > 0){
                    // Attempt to convert read data to JSON
                    JSONObject commands_object = (JSONObject) parser.parse(input.readUTF());
                    svrLogger.logDebug("MESSAGE FROM CLIENT: "+commands_object.toJSONString());

                    JSONObject resObj = new JSONObject();
                    resObj.put("MESSAGE FROM SERVER: ", handleCommand(commands_object));
                    output.writeUTF(resObj.toJSONString());
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function will check if the args format is correct,
     * if correct, set input as server port number
     * otherwise, use default setting 127.0.0.1: 3000
     * @param args Arguments from command line when run this Server
     */
    private static void parseArgs(String[] args){
        if (args.length == 2){
            String serverAddress = args[0];
            String serverPort = args[1];
            try {
                Integer prt = Integer.parseInt(serverPort);
                svrIPAddress = serverAddress;
                svrPort = prt;
            } catch (NumberFormatException e) {
                svrLogger.logError("Server port number should be an integer");
                System.exit(1);
            }
        } else {
            svrLogger.logWarn("Received Wrong arguments.\n" +
                    "A default server address and port will be used");
        }
    }

    /**
     * This function will deal with clients' initial login
     * @param command
     * @return 0 if failed to parse command, otherwise user/manager id should be larger than 0
     * TODO: make CreateWB and JoinWB transparently call each other
     */
    private static int parseLoginCommand(JSONObject command) {

        if (command.containsKey("command_name")) {
            svrLogger.logDebug("IT HAS A COMMAND NAME");
        }

        // If it is CreateWhiteBoard command, save this client as manager, and add to User list
        // return managerId
        if (command.get("command_name").equals("CreateWhiteBoard")) {
            svrLogger.logDebug("Client want to create a White Board");
            if (userList.getListSize() == 0) {
                createWhiteBoard(command);
            } else {
                joinWhiteBoard(command);
            }
        }

        // If it is JoinWhiteBoard command, add to User list for new users.
        if (command.get("command_name").equals("JoinWhiteBoard")) {
            svrLogger.logDebug("Client want to join a White Board");
            if (userList.getListSize() > 0) {
                joinWhiteBoard(command);
            } else {
                createWhiteBoard(command);
            }
        }
        return 0;
    }


    private static int createWhiteBoard(JSONObject command){
        int managerId = 0;
        if (userList.getListSize() == 0) {
            String managerName = (String) command.get("name");
            managerId = userList.addManager(managerName);
        } else {
            svrLogger.logError("This White Board already have a manager. Please try other server port");
        }
        return managerId;
    }
    private static int joinWhiteBoard(JSONObject command){
        int userId = 0;
        String userName = (String) command.get("username");

        // check if this user already contained in User list,
        // if not, create a new unique identifier,
        if (!userList.checkAUser(userName)) {
            userId = userList.addAUser(userName);
            svrLogger.logDebug("A new user: ID:  " + userId + " Username " + userName + " has been added");
        }
        return userId;
    }

    /**
     * Handling other requests from clients
     * @param commands
     * @return
     */
    private static String handleCommand(JSONObject commands){
        String commandName = commands.get("command_name").toString();
        switch(commandName)
            {
                case "drawRequest":
                    Shape x = (Shape) commands.get("shape");
                    objectsList.addAnObject(x);
                    break;
                case "kickOutUser":
                    int managerId = (int) commands.get("managerid");
                    int userid = (int) commands.get("kickwho");
                    userList.deleteAUser(managerId, userid);
                    break;
                case "eraseRequest":
                    Shape x1 = (Shape) commands.get("shape");
                    objectsList.deleteAnObject(x1);
                    break;

                default:
                    try
                    {
                        throw new Exception();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            }

        return null;
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
