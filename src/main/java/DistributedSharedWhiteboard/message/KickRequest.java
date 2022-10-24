package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Request from the Whiteboard manager to kick out a user
 */
@JsonSerializable
public class KickRequest extends Message {

    @JsonElement
    public String managerName;
    @JsonElement
    public String username;

    public KickRequest() {
    }

    public KickRequest(String managerName, String username) {
        this.managerName = managerName;
        this.username = username;
    }
}
