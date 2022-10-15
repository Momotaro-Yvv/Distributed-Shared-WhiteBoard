package com.example.distributedsharedwhiteboard.message;

import com.example.distributedsharedwhiteboard.Shape.JsonSerializable;
import com.example.distributedsharedwhiteboard.Shape.Shape;

@JsonSerializable
public class DrawRequest extends Message{
    @JsonElement
    public Shape shape;

    public int userId;
    public DrawRequest(){};

    public DrawRequest(Shape shape, Integer userId){
        this.shape = shape;
        this.userId = userId;
    };

}