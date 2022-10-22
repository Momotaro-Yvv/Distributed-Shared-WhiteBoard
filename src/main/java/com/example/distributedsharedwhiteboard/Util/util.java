package com.example.distributedsharedwhiteboard.Util;

import com.example.distributedsharedwhiteboard.Logger;
import com.example.distributedsharedwhiteboard.ShapeDrawing.ShapeDrawing;
import com.example.distributedsharedwhiteboard.message.ErrorMsg;
import com.example.distributedsharedwhiteboard.message.Message;
import com.example.distributedsharedwhiteboard.message.MessageFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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


    public static String TransferFromShape(ShapeDrawing shapeDrawing) {
        String jsonShape = shapeDrawing.toString();
        utilLogger.logDebug("Transferring from Shape to Json: "+ jsonShape);
        return jsonShape;
    };

    static public ShapeDrawing TransferToShape(String jsonShape) throws JsonSerializationException, IOException {
        if(jsonShape!=null) {
            ShapeDrawing shapeDrawing = (ShapeDrawing) MessageFactory.deserialize(jsonShape);
            utilLogger.logDebug("Transferring from Json to Shape: "+ shapeDrawing.toString());
            return shapeDrawing;
        } else {
            utilLogger.logDebug("The jsonShape is empty.");
            throw new IOException();
        }
    }

    static public List<ShapeDrawing> TransferFromJsonList(List<String> shapeDrawing) throws JsonSerializationException, IOException {
        List<ShapeDrawing> shapeDrawings= new ArrayList<>();
        for (String jsonShape: shapeDrawing){
            ShapeDrawing shape = TransferToShape(jsonShape);
            shapeDrawings.add(shape);
        }
        return shapeDrawings;
    }

    static public List<String> TransferToShapeList(List<ShapeDrawing> shapeDrawing) {
        List<String> jsonShapes= new ArrayList<>();
        for (ShapeDrawing shape: shapeDrawing){
            String jsonShape = TransferFromShape(shape);
            jsonShapes.add(jsonShape);
        }
        return jsonShapes;
    }
}

