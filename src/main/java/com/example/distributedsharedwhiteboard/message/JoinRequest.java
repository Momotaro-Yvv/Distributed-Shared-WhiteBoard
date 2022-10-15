package com.example.distributedsharedwhiteboard.message;

@JsonSerializable
public class JoinRequest extends Message{
    @JsonElement
    public String username;

    public JoinRequest() {};
    public JoinRequest(String username){
        this.username = username;
    }

}
