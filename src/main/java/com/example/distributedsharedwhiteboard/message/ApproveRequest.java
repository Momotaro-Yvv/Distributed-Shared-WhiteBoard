package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

import java.net.Socket;

/**
 * Request from server to manager that ask if the manager approve the joining of another user
 */

@JsonSerializable
public class ApproveRequest extends Message{

    @JsonElement
    public String username;
    @JsonElement
    public String clientIp;
    @JsonElement
    public Integer clientPort;

    public ApproveRequest(){};


    public ApproveRequest(String username, Socket socket) {
        this.username = username;
        this.clientIp = socket.getInetAddress().getHostAddress();
        this.clientPort = socket.getPort();
    }
}
