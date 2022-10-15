package com.example.distributedsharedwhiteboard.Shape;

import java.util.ArrayList;

@JsonSerializable
public class Path extends Shape{

    @JsonElement
    ArrayList<Double> xs, ys;

    public Path() {
    }

    public Path(ArrayList<Double> xs, ArrayList<Double> ys) {
        this.xs = xs;
        this.ys = ys;
    }
}
