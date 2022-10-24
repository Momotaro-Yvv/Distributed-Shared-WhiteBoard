package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Request from user that is leaving the application
 */
@JsonSerializable
public class QuitRequest extends Message {

    @JsonElement
    public String username;

    public QuitRequest() {
    }

    public QuitRequest(String username) {
        this.username = username;
    }
}
