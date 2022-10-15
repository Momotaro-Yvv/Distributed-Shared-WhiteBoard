package com.example.distributedsharedwhiteboard.message;

@JsonSerializable
public class CreateReply extends Message{

    @JsonElement
    public Integer userId;

    public CreateReply(){};

    public CreateReply(Integer userId){
        this.userId = userId;
    };

}
