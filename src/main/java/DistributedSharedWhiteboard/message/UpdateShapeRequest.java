package DistributedSharedWhiteboard.message;

import DistributedSharedWhiteboard.ShapeDrawing.ShapeDrawing;
import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

import static DistributedSharedWhiteboard.Util.util.TransferFromShape;

/**
 * Request from server to update any shape on the pane
 */
@JsonSerializable
public class UpdateShapeRequest extends Message {

    @JsonElement
    public String shape;
    @JsonElement
    public String byWhom;

    public UpdateShapeRequest() {
    }

    public UpdateShapeRequest(ShapeDrawing shapeDrawing, String byWhom) {
        this.shape = TransferFromShape(shapeDrawing);
        this.byWhom = byWhom;
    }

}
