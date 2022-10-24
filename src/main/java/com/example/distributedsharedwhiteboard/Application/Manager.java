package com.example.distributedsharedwhiteboard.Application;

import com.example.distributedsharedwhiteboard.message.*;

import com.example.distributedsharedwhiteboard.ShapeDrawing.ShapeDrawing;
import com.example.distributedsharedwhiteboard.message.DrawRequest;
import com.example.distributedsharedwhiteboard.message.KickRequest;
import com.example.distributedsharedwhiteboard.message.ReloadRequest;
import com.example.distributedsharedwhiteboard.message.TerminateWB;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static com.example.distributedsharedwhiteboard.Util.util.TransferToShapeList;
import static com.example.distributedsharedwhiteboard.Util.util.writeMsg;

public class Manager extends User {

    //Constructors
    public Manager (String username, Socket socket) throws IOException {
        super(username, socket);
    };

    //Getters

    //Setters

    //Methods
    void approveJoinRequest(boolean decision) {
        System.out.println("approveJoinRequest: " + decision);
        try {
            writeMsg(bufferedWriter, new ApproveReply(decision, "haiyao"));
        } catch (IOException e) {
            logger.logError("Failed to send SendMsgRequest...");
            throw new RuntimeException(e);
        }
    };

    void sendKickUserMsg(String userKicked){
        try {
            writeMsg(bufferedWriter, new KickRequest(this.userNameProperty().getValue(), userKicked));
        } catch (IOException e) {
            logger.logError("Failed to send KickRequest...");
            throw new RuntimeException(e);
        }
    };

    void sendTerminateMsg(){
        try {
            writeMsg(bufferedWriter, new TerminateWB(this.userNameProperty().getValue()));
        } catch (IOException e) {
            logger.logError("Failed to send TerminateWB Request...");
            throw new RuntimeException(e);
        }
    };

    void sendReloadRequest(List<ShapeDrawing> shapeDrawings){
        try {
            writeMsg(bufferedWriter, new ReloadRequest(TransferToShapeList(shapeDrawings), this.userNameProperty().getValue()));
        } catch (IOException e) {
            logger.logError("Failed to send Reload Request...");
            throw new RuntimeException(e);
        }
    }

}
