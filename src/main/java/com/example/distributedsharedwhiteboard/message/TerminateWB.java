package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

@JsonSerializable
public class TerminateWB extends Message{

    @JsonElement
    public Integer managerId;

    public TerminateWB(){};

    public TerminateWB(Integer managerId){
        this.managerId = managerId;
    };

}
