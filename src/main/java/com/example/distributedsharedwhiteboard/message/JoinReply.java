package com.example.distributedsharedwhiteboard.message;

@JsonSerializable
public class JoinReply extends Message{

    @JsonElement
    public Integer userId;

    public JoinReply(){};

    public JoinReply(Integer userId){
        this.userId = userId;
    };

}
