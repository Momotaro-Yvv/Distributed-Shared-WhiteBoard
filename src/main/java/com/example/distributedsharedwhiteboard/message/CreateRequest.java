package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Request from manager who wants to initialize a new white board
 */
@JsonSerializable
public class CreateRequest extends Message{
    @JsonElement
    public String username;

    public CreateRequest() {};
    public CreateRequest(String username){
        this.username = username;
    }

}
