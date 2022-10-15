package com.example.distributedsharedwhiteboard.Application.Controllers;

import com.example.distributedsharedwhiteboard.Application.Models.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

import java.net.InetAddress;
import java.util.List;

// FIXME:
// 1. bind view under userController - Yvonne
// 2. communication between server and clients through Message
// 3. use Json as ObjectList format -hai
//4. dialoge ui and controller-hai
// 5. Json Factory -hai
//5. Exception handling -hai, Yvonne
//6. a method used to transfer shape to Json, another a method used to transfer back
/**
 * A controller that handle both manager and user operation for the WhiteBoard Application
 */
public class userController {

    @FXML
    protected TextField msg;

    @FXML
    protected ListView<String> MsgHistory;

    protected ListView<String> UserList;
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
    protected Text text;

    @FXML
    protected Circle circle;

    @FXML
    protected Rectangle rectangle;

    @FXML
    protected Path path;

    @FXML
    protected Polygon polygon;

    protected String modeID;

    protected GraphicsContext gc;

    private double startX, startY, endX, endY;

    //Model
    User user;
    private String userName;
    InetAddress srvAddress;
    int srvPort;

    // message list
    private ObservableList<String> msgList = FXCollections.observableArrayList();

    // user list
    private ObservableList<String> userList = FXCollections.observableArrayList();

    // This allows the implementing class to perform any necessary post-processing on the content.
    // It also provides the controller with access to the resources that were used to load the
    // document and the location that was used to resolve relative paths within the document
    // (commonly equivalent to the location of the document itself).
    public void initialize() {

        user = new User(srvAddress, srvPort, userName);

        //link user with View
//        UserList.textProperty().bind(user.userlist);
////        MsgHistory
//        ObjectsList =pane.getChildren();


        // get controller
        userController controller = (userController)fxmlLoader.getController();

        // bind variables
        Bindings.bindContentBidirectional(msgList, controller.getMsgHistory().getItems());
        msgList.add("test only"); // now can access msgHistory via msgList

        Bindings.bindContentBidirectional(userList, controller.getUserList().getItems());
        userList.add("User 0");

        Application.Parameters params = getParameters();
        List<String> list = params.getRaw();
        controller.setUserName(list.get(2));
        controller.setSrvPort(Integer.parseInt(list.get(1)));
        controller.setSrvAddress(InetAddress.getByName(list.get(0)));

        // select freehand by default
        drawMode.getToggles().get(0).setSelected(true);

        // set default color as black
        colorPicker.setValue(Color.BLACK);

        // prepare shapes for canvas
        line = new Line();
        text = new Text();
        circle = new Circle();
        rectangle = new Rectangle();
        path = new Path();
        polygon = new Polygon();
    }

    @FXML
    protected void handleSendMsg(ActionEvent event) {

        // get msg
        String input = msg.getText();


        // check if text is empty
        if (input != null && input.length() != 0) {

            System.out.println(input);

            // add msg to message history
            MsgHistory.getItems().add("user54686: " + input);

            // clear input
            msg.clear();
        }
    }

    @FXML
    protected void handleQuit(ActionEvent event) {
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

                    // add shape to canvas via gc
                    gc = canvas.getGraphicsContext2D();
                    gc.setStroke(colorPicker.getValue());

                    gc.beginPath();
                    for (int i = 1; i < path.getElements().size(); i++) {
                        LineTo lt = (LineTo) path.getElements().get(i);
                        gc.lineTo(lt.getX(), lt.getY());
                    }
                    gc.stroke();

                    path.getElements().clear();
                    break;
                case "dm_line":
                    pane.getChildren().remove(line);

                    // add shape to canvas via gc
                    gc = canvas.getGraphicsContext2D();
                    gc.setStroke(colorPicker.getValue());
                    gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
                    break;
                case "dm_circle":
                    double sideLen = Math.min(Math.abs(endX - startX), Math.abs(endY - startY));
                    pane.getChildren().remove(circle);

                    // add shape to canvas via gc
                    gc = canvas.getGraphicsContext2D();
                    gc.setStroke(colorPicker.getValue());
                    gc.strokeOval(Math.min(startX, endX), Math.min(startY, endY), sideLen, sideLen);
                    break;
                case "dm_triangle":
                    double w = Math.abs(endX - startX);
                    pane.getChildren().remove(polygon);

                    // add shape to canvas via gc
                    gc = canvas.getGraphicsContext2D();
                    gc.setStroke(colorPicker.getValue());

                    gc.strokePolygon(new double[]{
                                    Math.min(startX, endX),
                                    Math.min(startX, endX) + 0.5 * w,
                                    Math.max(startX, endX)},
                            new double[]{
                                    Math.max(startY, endY),
                                    Math.min(startY, endY),
                                    Math.max(startY, endY)}, 3);
                    break;
                case "dm_rectangle":
                    pane.getChildren().remove(rectangle);

                    // add shape to canvas via gc
                    gc = canvas.getGraphicsContext2D();
                    gc.setStroke(colorPicker.getValue());
                    gc.strokeRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
                    break;
            }
        }
    }

    public ListView<String> getMsgHistory() {
        return MsgHistory;
    }

    public ListView<String> getUserList() {
        return UserList;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setSrvAddress(InetAddress srvAddress) {
        this.srvAddress = srvAddress;
    }

    public void setSrvPort(int srvPort) {
        this.srvPort = srvPort;
    }
}