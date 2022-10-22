package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

/**
 * Request from server to update an user removal in the user list
 */
@JsonSerializable
public class UpdateDeleteUserRequest extends Message{

    @JsonElement
    public String deleteUserName;

    public UpdateDeleteUserRequest(){}

    public UpdateDeleteUserRequest(String username){ this.deleteUserName = username;}
}
