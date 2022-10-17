package com.example.distributedsharedwhiteboard.Shape;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

@JsonSerializable
public class Text extends Shape{

    @JsonElement
    double x, y;

    @JsonElement
    String text;

    public Text() {
    }

    public Text(double x, double y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;
    }
}
