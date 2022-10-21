package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Request from server to manager that ask if the manager approve the joining of another user
 */

@JsonSerializable
public class ApproveRequest {

    @JsonElement
    public String username;

    public ApproveRequest(){};

    public ApproveRequest(String username) {this.username = username;}
}
