package com.example.distributedsharedwhiteboard.ShapeDrawing;

import com.example.distributedsharedwhiteboard.Util.JsonElement;
import com.example.distributedsharedwhiteboard.Util.JsonSerializable;

import javafx.scene.paint.Color;

@JsonSerializable
public class TextDrawing extends ShapeDrawing {

    @JsonElement
    public Double x, y;

    @JsonElement
    public String text;

    @JsonElement
    public Double red, green, blue, opacity;

    public TextDrawing() {
    }

    public TextDrawing(Double x, Double y, String text, Color color) {
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.opacity = color.getOpacity();

        this.x = x;
        this.y = y;
        this.text = text;
    }
}
