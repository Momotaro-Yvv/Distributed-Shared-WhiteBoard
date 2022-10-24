package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

/**
 * Request from manager to reopen a saved whiteboard
 */
@JsonSerializable
public class ReloadRequest extends Message {

    @JsonElement
    public String[] shapes;

    @JsonElement
    public String managerName;

    public ReloadRequest() {
    }

    public ReloadRequest(String[] shapes, String managerName) {
        this.shapes = shapes;
        this.managerName = managerName;
    }
}
