package DistributedSharedWhiteboard.ShapeDrawing;

import DistributedSharedWhiteboard.Util.JsonElement;
import DistributedSharedWhiteboard.Util.JsonSerializable;

import javafx.scene.paint.Color;

@JsonSerializable
public class RectangularDrawing extends ShapeDrawing {

    @JsonElement
    public Double x, y, width, height;

    @JsonElement
    public Double red, green, blue, opacity;

    public RectangularDrawing() {
    }

    public RectangularDrawing(Double x, Double y, Double width, Double height, Color color) {
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
        this.opacity = color.getOpacity();

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
