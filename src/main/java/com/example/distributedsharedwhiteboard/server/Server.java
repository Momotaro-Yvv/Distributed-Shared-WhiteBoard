package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ServerSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    static UserList userList;
    static String svrIPAddress;
    static int svrPort;
    static Logger logger = new Logger();
    static String welcomeMsg = " --- Welcome to DS White Board Server ---";

    public static void main(String[] args) {
        // parse command line arguments, save server ip & port
        // DEFAULT Server: 127.0.0.1: 3000
        parseArgs(args);

        //initialize an empty user list for tracking clients
        userList = new UserList();

        ServerSocketFactory factory = ServerSocketFactory.getDefault();
        try(ServerSocket server = factory.createServerSocket(svrPort)){

            logger.logInfo("Server - ["+svrIPAddress+":"+svrPort+"]");
            logger.logInfo("Waiting for client connection..");

            // Wait for connections.
            while(true){
                Socket client = server.accept();
                logger.logDebug("A new user has been connected");

                // Start a new thread for each connection
                Thread t = new Thread(() -> serveClient(client));
                t.start();
            }

        } catch (IOException e) {
            logger.logError("Failed to create Server Socket, please try again.");
            e.printStackTrace();
        }
    }

    private static void serveClient(Socket client)
    {
        try(Socket clientSocket = client)
        {
            logger.logDebug("Serving Client");

            // The JSON Parser
            JSONParser parser = new JSONParser();
            // Input stream
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            // Output Stream
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

            String clientMsg = input.readUTF();
            logger.logDebug("FROM CLIENT: "+ clientMsg );

            JSONObject command = (JSONObject) parser.parse(clientMsg);
            Integer requested_id = parseCommand(command);

            output.writeUTF("Server: Assignment id:" +requested_id +  welcomeMsg);
            // Receive more data..
            while(true){
                if(input.available() > 0){
                    // Attempt to convert read data to JSON
                    JSONObject commands_object = (JSONObject) parser.parse(input.readUTF());
                    System.out.println("COMMAND RECEIVED: "+commands_object.toJSONString());
                    JSONObject resObj = new JSONObject();
                    resObj.put("result", handleCommand(commands_object));

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
                logger.logError("Server port number should be an integer");
                System.exit(1);
            }
        } else {
            logger.logWarn("Received Wrong arguments.\n" +
                    "A default server address and port will be used");
        }
    }

    /**
     * This function will deal with clients' initial login and return
     * @param command
     * @return 0 if failed to parse command, otherwise user/manager id should be larger than 0
     */
    private static int parseCommand(JSONObject command) {

        if (command.containsKey("command_name")) {
            logger.logDebug("IT HAS A COMMAND NAME");
        }

        // If it is CreateWhiteBoard command, save this client as manager, and add to User list
        // return managerId
        if (command.get("command_name").equals("CreateWhiteBoard")) {
            int managerId = 0;
            if (userList.getListSize() == 0) {
                String managerName = (String) command.get("name");
                managerId = userList.addManager(managerName);
            } else {
                logger.logError("This White Board already have a manager. Please try other server port");
            }
            return managerId;
        }

        // If it is JoinWhiteBoard command, add to User list for new users.
        if (command.get("command_name").equals("JoinWhiteBoard")) {
            int userId = 0;
            String userName = (String) command.get("name");

            // check if this user already contained in User list,
            // if not, create a new unique identifier,
            if (!userList.checkAUser(userName)) {
                userId = userList.addAUser(userName);
                logger.logDebug("A new user" + userId + " " + userName + "has been added");
            }
            return userId;
        }
        return 0;
    }

    /**
     * Handling other requests from clients
     * @param commands
     * @return
     */
    private static String handleCommand(JSONObject commands){
        return null;
    }

            // Initialize a unique identifier for each user
//            Math math = new Math();
//            Integer firstInt = Integer.parseInt(command.get("first_integer").toString());
//            Integer secondInt = Integer.parseInt(command.get("second_integer").toString());
//
//            switch((String) command.get("method_name"))
//            {
//                case "add":
//                    result = math.add(firstInt,secondInt);
//                    break;
//                case "multiply":
//                    result = math.multiply(firstInt,secondInt);
//                    break;
//                case "subtract":
//                    result = math.subtract(firstInt,secondInt);
//                    break;
//                default:
//                    try
//                    {
//                        throw new Exception();
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//            }
}
