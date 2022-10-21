package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Request from the Whiteboard manager to terminate the whole application
 */
@JsonSerializable
public class TerminateWB extends Message{

    @JsonElement
    public String managerName;

    public TerminateWB(){};

    public TerminateWB(String managerName){
        this.managerName = managerName;
    };

}
