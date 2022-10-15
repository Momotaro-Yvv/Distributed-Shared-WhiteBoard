package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Shape.JsonSerializable;

@JsonSerializable
public class KickRequest extends Message{

    @JsonElement
    public Integer managerId;
    @JsonElement
    public Integer userId;
    public KickRequest(){};

    public KickRequest(Integer managerId, Integer userId){
        this.managerId = managerId;
        this.userId = userId;
    };

}
