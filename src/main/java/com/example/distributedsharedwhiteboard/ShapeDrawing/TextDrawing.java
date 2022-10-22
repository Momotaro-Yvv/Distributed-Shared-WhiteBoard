package com.example.distributedsharedwhiteboard.ShapeDrawing;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

@JsonSerializable
public class TextDrawing extends ShapeDrawing {

    @JsonElement
    public double x, y;

    @JsonElement
    public String text;

    public TextDrawing() {
    }

    public TextDrawing(double x, double y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }
}
