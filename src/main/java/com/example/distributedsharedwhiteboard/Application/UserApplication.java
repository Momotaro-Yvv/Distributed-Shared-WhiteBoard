package com.example.distributedsharedwhiteboard.Application;

import com.example.distributedsharedwhiteboard.client.JoinWhiteBoard;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;


public class UserApplication extends Application {
    private static String userName;
    static InetAddress srvAddress;
    static int srvPort;
    JSONObject res;

    @Override
    public void start(Stage stage) throws IOException {

        try {
            Socket socket = new Socket(srvAddress, srvPort);

            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Send JoinWhiteBoard request to Server
            JSONObject newCommand = new JSONObject();
            newCommand.put("command_name", "JoinWhiteBoard");
            newCommand.put("username", userName);
            output.writeUTF(newCommand.toJSONString());
            output.flush();

            // Receive JoinWhiteBoard reply from server.
            String reply = input.readUTF();
            JSONParser parser = new JSONParser();
            res = (JSONObject) parser.parse(input.readUTF());


            // TODO: HOW TO CONNECT THESE USER APLLICATION WITH UERCONTROLLER??
            /*
             * On enter the application , Log some information about the configuration settings.
             */
//            String welcomeMsg = " --- Welcome to DS White Board ---";
//            String connectionInfo =  "Server: " + Server.getSvrIPAddress() + ":" + Server.getSvrPort();
//            String assignedUserId = "Your user ID: " + res.get("userid");

            // add msg to message history

        } catch (IOException e) {
            System.out.println("Client received IO exception on socket.");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(UserApplication.class.getResource("hello-view_user.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
//        scene.getStylesheets().add(Objects.requireNonNull(UserApplication.class.getResource("styleSheet.css")).toString());
        Image icon = new Image("icon.png");
        stage.getIcons().add(icon);
        stage.setTitle("WhiteBoard - User Window");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        srvAddress = JoinWhiteBoard.getSrvAddress();
        srvPort = JoinWhiteBoard.getSrvPort();
        userName = JoinWhiteBoard.getUserName();
        launch(args);
    }
}