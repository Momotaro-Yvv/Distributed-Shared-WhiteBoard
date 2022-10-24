package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Request from server to update an addition in the user list
 */
@JsonSerializable
public class UpdateUserlistRequest extends Message {

    @JsonElement
    public String newUserName;

    public UpdateUserlistRequest() {
    }

    public UpdateUserlistRequest(String newUserName) {
        this.newUserName = newUserName;
    }
}
