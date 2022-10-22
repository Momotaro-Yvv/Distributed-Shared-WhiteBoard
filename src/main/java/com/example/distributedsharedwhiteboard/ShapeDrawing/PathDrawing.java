package com.example.distributedsharedwhiteboard.ShapeDrawing;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

import java.util.ArrayList;

@JsonSerializable
public class PathDrawing extends ShapeDrawing {

    @JsonElement
    public double[] xs, ys;

    public PathDrawing() {
    }

    public PathDrawing(double[] xs, double[] ys) {
        this.xs = xs;
        this.ys = ys;
    }
}
