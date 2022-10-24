package com.example.distributedsharedwhiteboard.server;

import com.example.distributedsharedwhiteboard.Application.User;
import com.example.distributedsharedwhiteboard.ShapeDrawing.ShapeDrawing;
import com.example.distributedsharedwhiteboard.message.Message;
import com.example.distributedsharedwhiteboard.message.UpdateMsgRequest;
import com.example.distributedsharedwhiteboard.message.UpdateShapeRequest;
import com.example.distributedsharedwhiteboard.message.UpdateUserlistRequest;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingDeque;

import static com.example.distributedsharedwhiteboard.Util.util.writeMsg;

public class UpdateThread extends Thread{

    LinkedBlockingDeque<Message> incomingUpdates;
    UserList userList;
    public  UpdateThread(LinkedBlockingDeque<Message> incomingUpdates, UserList userList){
        this.incomingUpdates = incomingUpdates;
        this.userList =  userList;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Message message = incomingUpdates.take();
                for (Socket other : userList.getAllSockets()) {
                    DataOutputStream out = new DataOutputStream(other.getOutputStream());
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                    writeMsg(bw, message);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}