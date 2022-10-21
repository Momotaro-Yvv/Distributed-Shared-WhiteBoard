package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Request from a user to join an existing whiteboard
 */
@JsonSerializable
public class JoinRequest extends Message{
    @JsonElement
    public String username;

    public JoinRequest() {};
    public JoinRequest(String username){
        this.username = username;
    }

}
