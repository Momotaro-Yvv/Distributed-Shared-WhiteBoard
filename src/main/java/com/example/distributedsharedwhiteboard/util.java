package com.example.distributedsharedwhiteboard;

import com.example.distributedsharedwhiteboard.message.JsonSerializationException;
import com.example.distributedsharedwhiteboard.message.Message;
import com.example.distributedsharedwhiteboard.message.MessageFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class util {
    static Logger utilLogger = new Logger();

    /**
     * Source: Aaron Harwood - DS Project 1 P2P File Sharing System
     */
    public static void writeMsg(BufferedWriter bufferedWriter, Message msg) throws IOException {

        utilLogger.logDebug("sending: "+msg.toString());
        bufferedWriter.write(msg.toString());
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    /**
     * Source: Aaron Harwood - DS Project 1 P2P File Sharing System
     */
    static public Message readMsg(BufferedReader bufferedReader) throws IOException, JsonSerializationException {
        String jsonStr = bufferedReader.readLine();
        if(jsonStr!=null) {
            Message msg = (Message) MessageFactory.deserialize(jsonStr);
            utilLogger.logDebug("received: "+msg.toString());
            return msg;
        } else {
            throw new IOException();
        }
    }

//    static Message writeRequestMsg(DataInputStream inputStream, DataOutputStream outputStream){
//        // Send JoinWhiteBoard request to Server
//        JSONObject newRequest = new JSONObject();
//        newRequest.put("command_name", "JoinWhiteBoard");
//        newRequest.put("username", userName);
//        outputStream.writeUTF(newRequest.toJSONString());
//        outputStream.flush();
//
//        // Receive JoinWhiteBoard reply from server.
//        JSONParser parser = new JSONParser();
//        reply = (JSONObject) parser.parse(inputStream.readUTF());
//        userId = (int) reply.get("userid");
//    };
}

