package com.example.distributedsharedwhiteboard.message;

@JsonSerializable
public class CreateRequest extends Message{
    @JsonElement
    public String username;

    public CreateRequest() {};
    public CreateRequest(String username){
        this.username = username;
    }

}
