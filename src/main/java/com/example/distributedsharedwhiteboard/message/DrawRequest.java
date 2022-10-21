package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Shape.Shape;
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

    public String username;
    public DrawRequest(){};

    public DrawRequest(Shape shape, String username){
        this.shape = TransferFromShape(shape);
        this.username = username;
    };

}