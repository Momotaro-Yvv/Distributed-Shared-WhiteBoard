package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

import java.util.List;

/**
 * Request from manager to reopen a saved whiteboard
 */
@JsonSerializable
public class ReloadRequest extends Message{

    @JsonElement
    public String[] shapes;

    @JsonElement
    public String managerName;

    public ReloadRequest(){}

    public ReloadRequest(String[] shapes, String managerName){
        this.shapes = shapes;
        this.managerName = managerName;
    }
}
