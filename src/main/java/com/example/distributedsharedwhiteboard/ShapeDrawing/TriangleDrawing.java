package com.example.distributedsharedwhiteboard.ShapeDrawing;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

import java.util.ArrayList;

import javafx.scene.paint.Color;

@JsonSerializable
public class TriangleDrawing extends ShapeDrawing {

    @JsonElement
    public Double[] xs, ys;

    @JsonElement
    public Double red, green, blue, opacity;

    public TriangleDrawing() {
    }

    public TriangleDrawing(Double[] xs, Double[] ys, Color color) {
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.opacity = color.getOpacity();

        this.xs = xs;
        this.ys = ys;
    }
}
