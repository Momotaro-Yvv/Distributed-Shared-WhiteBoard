package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.ShapeDrawing.ShapeDrawing;
import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

import static com.example.distributedsharedwhiteboard.Util.util.TransferFromShape;

/**
 * Request from server to update any shape on the pane
 */
@JsonSerializable
public class UpdateShapeRequest extends Message{

    @JsonElement
    public String shape;
    @JsonElement
    public String byWhom;

    public UpdateShapeRequest(){}

    public UpdateShapeRequest(ShapeDrawing shapeDrawing, String byWhom){
        this.shape = TransferFromShape(shapeDrawing);
        this.byWhom = byWhom;
    }

}
