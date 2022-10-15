package com.example.distributedsharedwhiteboard.message;

@JsonSerializable
public class TerminateWB extends Message{

    @JsonElement
    public Integer managerId;

    public TerminateWB(){};

    public TerminateWB(Integer managerId){
        this.managerId = managerId;
    };

}
