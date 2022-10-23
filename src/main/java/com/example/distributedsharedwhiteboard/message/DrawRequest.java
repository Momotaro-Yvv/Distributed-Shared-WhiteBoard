package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.ShapeDrawing.ShapeDrawing;
import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

import static com.example.distributedsharedwhiteboard.Util.util.TransferFromShape;

/**
 * Request from user to draw some shape(including circle, line, freeDraw, rectangular, text, triangle) on the whiteboard
 */
@JsonSerializable
public class DrawRequest extends Message{
    @JsonElement
    public String shape;

    @JsonElement
    public String username;
    public DrawRequest(){};

    public DrawRequest(String username,ShapeDrawing shapeDrawing){
        this.shape = TransferFromShape(shapeDrawing);
        this.username = username;
    };

}