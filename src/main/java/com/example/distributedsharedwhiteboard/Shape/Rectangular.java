package com.example.distributedsharedwhiteboard.Shape;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

@JsonSerializable
public class Rectangular extends Shape{

    @JsonElement
    double x, y, width, height;

    public Rectangular() {
    }

    public Rectangular(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
