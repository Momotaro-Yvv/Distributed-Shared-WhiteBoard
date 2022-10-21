package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Request from user that is leaving the application
 */
@JsonSerializable
public class QuitRequest extends Message{

    @JsonElement
    public String username;

    public QuitRequest(){};

    public QuitRequest(String username){
        this.username = username;
    };

}
