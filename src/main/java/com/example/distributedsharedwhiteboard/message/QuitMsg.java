package com.example.distributedsharedwhiteboard.message;

@JsonSerializable
public class QuitMsg extends Message{

    @JsonElement
    public Integer userId;

    public QuitMsg(){};

    public QuitMsg(Integer userId){
        this.userId = userId;
    };

}
