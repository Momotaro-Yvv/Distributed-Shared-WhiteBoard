package com.example.distributedsharedwhiteboard.ShapeDrawing;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

@JsonSerializable
public class RectangularDrawing extends ShapeDrawing {

    @JsonElement
    public double x, y, width, height;

    public RectangularDrawing() {
    }

    public RectangularDrawing(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
