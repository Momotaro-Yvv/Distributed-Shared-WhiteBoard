package com.example.distributedsharedwhiteboard.Application;

import com.example.distributedsharedwhiteboard.ShapeDrawing.*;
import com.example.distributedsharedwhiteboard.client.JoinWhiteBoard;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.*;
import javafx.scene.shape.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A controller that handle both manager and user operation for the WhiteBoard Application
 */
public class userController {

    @FXML
    protected TextField msg;

    @FXML
    protected ListView<String> msgHistory;

    @FXML
    protected ListView<String> userList;

    @FXML
    protected ColorPicker colorPicker;

    @FXML
    protected ToggleGroup drawMode;

    @FXML
    protected Pane pane;

    @FXML
    protected Canvas canvas;

    // shapes for drawing
    @FXML
    protected Line line;

    @FXML
    protected TextField textfield;

    @FXML
    protected Circle circle;

    @FXML
    protected Rectangle rectangle;

    @FXML
    protected Path path;

    @FXML
    protected Polygon polygon;

    protected boolean isUnsaved = false;

    protected GraphicsContext gc;

    protected String modeID;

    protected double startX;
    protected double startY;
    protected double endX;
    protected double endY;

    protected ObservableList<ShapeDrawing> drawedShapes;
    protected ObservableList<ShapeDrawing> undrawedShape;

    protected ObservableList<String> todoEvents;

    // Model
    private User user;

    /**
     * Convert the given Double array to double array
     * @param arr - Double array to convert
     * @return double array converted
     */
    protected double[] DoubleTodouble(Double[] arr) {
        double[] value = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            value[i] = arr[i];
        }
        return value;
    }

    /**
     * This method sets defaults values and add required listeners for fxml elements to ensure
     * fxml elements are working properly.
     */
    protected void setUp() {
        // prepare shape list
        undrawedShape = FXCollections.observableArrayList();
        drawedShapes = FXCollections.observableArrayList();

        todoEvents = FXCollections.observableArrayList();

        // select freehand by default
        drawMode.getToggles().get(0).setSelected(true);

        // prepare graphic handle
        gc = canvas.getGraphicsContext2D();

        // prepare color picker
        colorPicker.setValue(Color.BLACK);
        colorPicker.getStyleClass().add("button");
        colorPicker.valueProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable,
                                Color oldValue, Color newValue) {
                if (oldValue != newValue) {
                    gc.setStroke(newValue);
                    gc.setFill(newValue);
                }
            }
        });

        // prepare shapes for drawing
        line = new Line();
        textfield = new TextField();
        circle = new Circle();
        rectangle = new Rectangle();
        path = new Path();
        polygon = new Polygon();

        // set listeners to textfield
        textfield.setOnAction(e -> {
            // draw text when user press enter
            drawText();
        });

        textfield.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if( newVal == false) {
                // draw text when user unfocus the textfield
                drawText();
            }
        });
    }

    /**
     * This method is called to initialize a controller after its root element has been completely processed
     * which means all fxml elements have been successfully loaded.
     */
    public void initialize() {
        user = JoinWhiteBoard.getUser();
        System.out.println("User set up!");

        // set up default settings
        setUp();

        ObservableList<ShapeDrawing> startList = user.getObjectList();
        for (ShapeDrawing shapeDrawing : startList) {
            drawNewShape(shapeDrawing);
        }

        // bind variables
        Bindings.bindContentBidirectional(msgHistory.getItems(), user.getMsgList());
        user.addMsgItem("test only : message"); // now can access msgHistory via msgList

        Bindings.bindContentBidirectional(userList.getItems(), user.getUserList());
        user.addUserItem("Test only : user1");

        user.getUndrawedList().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change c) {

                while (c.next()) {

                    // if anything was added to list
                    if (c.wasAdded()) {
                        for (Object s : c.getAddedSubList()) {
                            // ask user to send a updateRequest to server

                            drawNewShape((ShapeDrawing) s);
                        }
                    }
                }
            }

        });
    }

    @FXML
    protected void handleSendMsg(ActionEvent event) {

        // get msg
        String input = msg.getText();


        // check if text is empty
        if (input != null && input.length() != 0) {

            // add msg to message history
            msgHistory.getItems().add("Me: " + input);
            user.sendChatMsg(input);
            // clear input
            msg.clear();
        }
    }

    @FXML
    protected void handleQuit(ActionEvent event) {

        user.sendQuitMsg();
        Platform.exit();
    }

    @FXML
    protected void handleDraw(MouseEvent event) {

        // check mouse event
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {

            // set drawing mode
            modeID = ((ToggleButton)(drawMode.getSelectedToggle())).getId();

            // set start x and y
            startX = event.getX();
            startY = event.getY();

            switch (modeID) {
                case "dm_free":
                    pane.getChildren().add(path);

                    path.setStroke(colorPicker.getValue());

                    path.getElements().add(new MoveTo(startX, startY));
                    break;
                case "dm_text":
                    // if a textbox is already existed
                    if (pane.getChildren().contains(textfield)) {

                        drawText();
                    }
                    pane.getChildren().add(textfield);
                    textfield.setLayoutX(startX);
                    textfield.setLayoutY(startY);
                    textfield.requestFocus();
                    break;
                case "dm_line":
                    // add shape to pane
                    pane.getChildren().add(line);

                    // set color
                    line.setStroke(colorPicker.getValue());

                    // set coordinates for shape
                    line.setStartX(startX);
                    line.setStartY(startY);
                    line.setEndX(startX);
                    line.setEndY(startY);
                    break;
                case "dm_circle":
                    pane.getChildren().add(circle);

                    circle.setStroke(colorPicker.getValue());
                    circle.setFill(null);

                    circle.setCenterX(startX);
                    circle.setCenterY(startY);
                    circle.setRadius(0.f);
                    break;
                case "dm_triangle":
                    pane.getChildren().add(polygon);

                    polygon.setStroke(colorPicker.getValue());
                    polygon.setFill(null);
                    break;
                case "dm_rectangle":
                    // add shape to pane
                    pane.getChildren().add(rectangle);

                    // set color
                    rectangle.setStroke(colorPicker.getValue());
                    rectangle.setFill(null);

                    // set coordinates for shape
                    rectangle.setX(startX);
                    rectangle.setY(startY);
                    rectangle.setWidth(0f);
                    rectangle.setHeight(0f);
                    break;
            }

        }else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {

            endX = event.getX();
            endY = event.getY();

            switch (modeID) {
                case "dm_free":
                    path.getElements().add(new LineTo(endX, endY));
                    break;
                case "dm_line":
                    line.setEndX(endX);
                    line.setEndY(endY);
                    break;
                case "dm_circle":

                    double sideLen = Math.min(Math.abs(endX - startX), Math.abs(endY - startY));

                    circle.setCenterX(Math.min(startX, endX) + (sideLen / 2));
                    circle.setCenterY(Math.min(startY, endY) + (sideLen / 2));
                    circle.setRadius(sideLen / 2);
                    break;
                case "dm_triangle":
                    double w = Math.abs(endX - startX);
                    polygon.getPoints().clear();
                    polygon.getPoints().addAll(new Double[]{
                            Math.min(startX, endX), Math.max(startY, endY),
                            Math.min(startX, endX) + 0.5 * w, Math.min(startY, endY),
                            Math.max(startX, endX), Math.max(startY, endY) });
                    break;
                case "dm_rectangle":

                    rectangle.setX(Math.min(startX, endX));
                    rectangle.setY(Math.min(startY, endY));

                    rectangle.setWidth(Math.abs(endX - startX));
                    rectangle.setHeight(Math.abs(endY - startY));
                    break;
            }

        } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {

            endX = event.getX();
            endY = event.getY();

            switch (modeID) {
                case "dm_free":
                    pane.getChildren().remove(path);

                    int size = path.getElements().size();
                    Double[] xs = new Double[size];
                    Double[] ys = new Double[size];

                    gc.beginPath();

                    MoveTo mt = (MoveTo) path.getElements().get(0);
                    gc.moveTo(mt.getX(), mt.getY());
                    xs[0] = mt.getX();
                    ys[0] = mt.getY();

                    for (int i = 1; i < size; i++) {
                        LineTo lt = (LineTo) path.getElements().get(i);
                        gc.lineTo(lt.getX(), lt.getY());
                        xs[i] = lt.getX();
                        ys[i] = lt.getY();
                    }
                    gc.stroke();
                    PathDrawing pathDrawing = new PathDrawing(xs, ys, colorPicker.getValue());
                    drawedShapes.add(pathDrawing);
                    user.sendDrawMsg(pathDrawing);

                    path.getElements().clear();
                    break;
                case "dm_line":
                    pane.getChildren().remove(line);

                    LineDrawing lineDrawing = new LineDrawing(line.getStartX(), line.getStartY(), line.getEndX(),
                            line.getEndY(), colorPicker.getValue());
                    drawedShapes.add(lineDrawing);
                    user.sendDrawMsg(lineDrawing);

                    // add shape to canvas via gc
                    gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                    break;
                case "dm_circle":
                    double sideLen = Math.min(Math.abs(endX - startX), Math.abs(endY - startY));
                    pane.getChildren().remove(circle);

                    CircleDrawing circleDrawing = new CircleDrawing(Math.min(startX, endX), Math.min(startY, endY),
                            sideLen, colorPicker.getValue());
                    drawedShapes.add(circleDrawing);
                    user.sendDrawMsg(circleDrawing);

                    // add shape to canvas via gc
                    gc.strokeOval(Math.min(startX, endX), Math.min(startY, endY), sideLen, sideLen);
                    break;
                case "dm_triangle":
                    double w = Math.abs(endX - startX);
                    pane.getChildren().remove(polygon);

                    Double[] tri_xs = new Double[]{
                            Math.min(startX, endX),
                            Math.min(startX, endX) + 0.5 * w,
                            Math.max(startX, endX)};
                    Double[] tri_ys = new Double[]{
                            Math.max(startY, endY),
                            Math.min(startY, endY),
                            Math.max(startY, endY)
                    };

                    TriangleDrawing triangleDrawing = new TriangleDrawing(tri_xs, tri_ys, colorPicker.getValue());
                    user.sendDrawMsg(triangleDrawing);
                    drawedShapes.add(triangleDrawing);
                    // add shape to canvas via gc
                    gc.strokePolygon(DoubleTodouble(tri_xs), DoubleTodouble(tri_ys), 3);
                    break;
                case "dm_rectangle":
                    pane.getChildren().remove(rectangle);

                    RectangularDrawing rectangleDrawing = new RectangularDrawing(rectangle.getX(), rectangle.getY(),
                            rectangle.getWidth(), rectangle.getHeight(), colorPicker.getValue());
                    drawedShapes.add(rectangleDrawing);
                    user.sendDrawMsg(rectangleDrawing);
                    // add shape to canvas via gc
                    gc.strokeRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
                    break;
            }
        }

        // update unsave status
        isUnsaved = true;
    }

    /**
     * Invoke this method to draw a shape onto whiteboard
     * @param shape - shape to draw
     */
    protected void drawNewShape(ShapeDrawing shape) {

        Color oldColor = (Color)gc.getStroke();

        // draw shape based on its type
        switch (shape.getClass().getSimpleName()) {
            case "CircleDrawing":
                CircleDrawing circleDrawing = (CircleDrawing) shape;
                gc.setStroke(new Color(circleDrawing.red, circleDrawing.green, circleDrawing.blue, circleDrawing.opacity));
                gc.strokeOval(circleDrawing.x, circleDrawing.y, circleDrawing.sideLen, circleDrawing.sideLen);
                break;
            case "LineDrawing":
                LineDrawing lineDrawing = (LineDrawing) shape;
                gc.setStroke(new Color(lineDrawing.red, lineDrawing.green, lineDrawing.blue, lineDrawing.opacity));
                gc.strokeLine(lineDrawing.startX, lineDrawing.startY, lineDrawing.endX, lineDrawing.endY);
                break;
            case "PathDrawing":
                PathDrawing pathDrawing = (PathDrawing) shape;
                gc.setStroke(new Color(pathDrawing.red, pathDrawing.green, pathDrawing.blue, pathDrawing.opacity));
                gc.beginPath();

                gc.moveTo(pathDrawing.xs[0], pathDrawing.ys[0]);

                for (int i = 1; i < pathDrawing.xs.length; i++) {
                    gc.lineTo(pathDrawing.xs[i], pathDrawing.ys[i]);
                }
                gc.stroke();
                break;
            case "RectangularDrawing":
                RectangularDrawing rectangularDrawing = (RectangularDrawing) shape;
                gc.setStroke(new Color(rectangularDrawing.red, rectangularDrawing.green, rectangularDrawing.blue, rectangularDrawing.opacity));
                gc.strokeRect(rectangularDrawing.x, rectangularDrawing.y, rectangularDrawing.width, rectangularDrawing.height);
                break;
            case "TextDrawing":
                TextDrawing textDrawing = (TextDrawing) shape;
                gc.setFill(new Color(textDrawing.red, textDrawing.green, textDrawing.blue, textDrawing.opacity));
                gc.fillText(textDrawing.text, textDrawing.x, textDrawing.y);
                break;
            case "TriangleDrawing":
                TriangleDrawing triangleDrawing = (TriangleDrawing) shape;
                gc.setStroke(new Color(triangleDrawing.red, triangleDrawing.green, triangleDrawing.blue, triangleDrawing.opacity));
                gc.strokePolygon(DoubleTodouble(triangleDrawing.xs), DoubleTodouble(triangleDrawing.ys), 3);
                break;
        }

        // reset to oldColor
        gc.setStroke(oldColor);
        gc.setFill(oldColor);

        // add drawed shape to list
        drawedShapes.add(shape);

        // update unsave status
        isUnsaved = true;
    }

    /**
     * Invoke this method to draw the text from textfield. If textfield is empty then
     * no drawing is performed.
     */
    protected void drawText() {

        String text = textfield.getText();

        if (text.length() > 0) {
            gc.fillText(text, textfield.getLayoutX(), textfield.getLayoutY());

            // add drawed shape to list
            TextDrawing drawing = new TextDrawing(textfield.getLayoutX(), textfield.getLayoutY(), text, colorPicker.getValue());
            user.sendDrawMsg(drawing);
            drawedShapes.add(drawing);

            // update unsave status
            isUnsaved = true;

            textfield.clear();
        }

        pane.getChildren().remove(textfield);
    }

    /**
     * Invoke this message to display a dialog with error message
     * @param message - the message to display
     */
    protected void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setHeaderText("An error occurred.");
        alert.setContentText(message);

        alert.showAndWait();
    }

    /**
     * Invoke this message to display a dialog with info message
     * @param message - the message to display
     */
    protected void showInfoDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info Dialog");
        alert.setContentText(message);

        alert.showAndWait();
    }

    public void setUser(User user) {
        this.user = user;
    }

}