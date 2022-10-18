package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Shape.Shape;
import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

import static com.example.distributedsharedwhiteboard.Util.util.TransferFromShape;

@JsonSerializable
public class DrawRequest extends Message{
    @JsonElement
    public String shape;

    public int userId;
    public DrawRequest(){};

    public DrawRequest(Shape shape, Integer userId){
        this.shape = TransferFromShape(shape);
        this.userId = userId;
    };

}