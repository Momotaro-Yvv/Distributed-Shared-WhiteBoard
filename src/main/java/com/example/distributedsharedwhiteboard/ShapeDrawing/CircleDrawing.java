package com.example.distributedsharedwhiteboard.ShapeDrawing;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

@JsonSerializable
public class CircleDrawing extends ShapeDrawing {

    @JsonElement
    public double x, y, sideLen;

    public CircleDrawing() {
    }

    public CircleDrawing(double x, double y, double sideLen) {
        this.x = x;
        this.y = y;
        this.sideLen = sideLen;
    }
}
