package DistributedSharedWhiteboard.ShapeDrawing;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

import javafx.scene.paint.Color;

@JsonSerializable
public class CircleDrawing extends ShapeDrawing {

    @JsonElement
    public Double x, y, sideLen;

    @JsonElement
    public Double red, green, blue, opacity;

    public CircleDrawing() {
    }

    public CircleDrawing(Double x, Double y, Double sideLen, Color color) {
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.opacity = color.getOpacity();

        this.x = x;
        this.y = y;
        this.sideLen = sideLen;
    }
}
