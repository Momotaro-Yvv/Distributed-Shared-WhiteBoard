package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.ShapeDrawing.ShapeDrawing;
import DistributedSharedWhiteboard.Util.JsonSerializable;

import static DistributedSharedWhiteboard.Util.util.TransferFromShape;

/**
 * Request from user to draw some shape(including circle, line, freeDraw, rectangular, text, triangle) on the whiteboard
 */
@JsonSerializable
public class DrawRequest extends Message {
    @JsonElement
    public String shape;

    @JsonElement
    public String username;

    public DrawRequest() {
    }

    public DrawRequest(String username, ShapeDrawing shapeDrawing) {
        this.shape = TransferFromShape(shapeDrawing);
        this.username = username;
    }

}