package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Request from server to manager that ask if the manager approve the joining of another user
 */

@JsonSerializable
public class ApproveRequest extends Message {

    @JsonElement
    public String username;

    public ApproveRequest() {
    }

    public ApproveRequest(String username) {
        this.username = username;
    }
}
