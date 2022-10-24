package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Disconnection Msg from server to all clients that the application has been terminated
 */

@JsonSerializable
public class Goodbye extends Message {

    @JsonElement
    public String goodbye;

    public Goodbye() {
    }

    public Goodbye(String goodbye) {
        this.goodbye = goodbye;
    }
}
