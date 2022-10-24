package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Request from server to update an user removal in the user list
 */
@JsonSerializable
public class UpdateDeleteUserRequest extends Message {

    @JsonElement
    public String deleteUserName;

    public UpdateDeleteUserRequest() {
    }

    public UpdateDeleteUserRequest(String username) {
        this.deleteUserName = username;
    }
}
