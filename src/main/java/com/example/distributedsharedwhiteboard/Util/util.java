package com.example.distributedsharedwhiteboard.Util;

import com.example.distributedsharedwhiteboard.Logger;
import com.example.distributedsharedwhiteboard.message.ErrorMsg;
import com.example.distributedsharedwhiteboard.message.Message;
import com.example.distributedsharedwhiteboard.message.MessageFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class util {
    static Logger utilLogger = new Logger();

    /**
     * Code Source: Aaron Harwood - DS Project 1 P2P File Sharing System
     */
    public static void writeMsg(BufferedWriter bufferedWriter, Message msg) throws IOException {

        utilLogger.logDebug("sending: "+msg.toString());
        bufferedWriter.write(msg.toString());
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    /**
     * Code Source: Aaron Harwood - DS Project 1 P2P File Sharing System
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

    static public Message SendAndRead(BufferedWriter bufferedWriter, BufferedReader bufferedReader, Message request)
            throws IOException {

        // Send JoinWhiteBoard request to Server
        util.writeMsg(bufferedWriter,request);

        // Receive JoinWhiteBoard reply from server.
        Message msgFromServer = null;

        try {
            msgFromServer = readMsg(bufferedReader);
        } catch (JsonSerializationException | IOException e1) {
            writeMsg(bufferedWriter, new ErrorMsg("Invalid message"));
        }
        return msgFromServer;
    }


//    static public JSONObject ShapeToJson(Shape shape){
//
//    }
//    static public Shape JsonToShape(JSONObject jsonObject){
//    }
}

