package com.example.distributedsharedwhiteboard.Application;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;


/**
 * A controller that handle both manager and user operation for the WhiteBoard Application
 */
public class userController {
    @FXML
    private TextField message;

    @FXML
    private ListView MsgHistory;

    @FXML
    private Pane canvas;

    @FXML
    private Line line;

    @FXML
    protected void handleQuitButtonAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    protected void handleSendMessageAction(ActionEvent event) {

        // get msg
        String msg = message.getText();

        // check if text is empty
        if (msg != null && msg.length() != 0) {

            // add msg to message history
            ObservableList<String> msgs = MsgHistory.getItems();
            msgs.addAll("user54686: " + msg);
            MsgHistory.setItems(msgs);
            System.out.println("Message: " + msg);

            // clear input
            message.clear();
        }
    }

    @FXML
    protected void handleDrawAction(MouseEvent event) {

        // check mouse event
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {

            line.setStartX(event.getX());
            line.setStartY(event.getY());
        }else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED || event.getEventType() == MouseEvent.MOUSE_RELEASED) {

            line.setEndX(event.getX());
            line.setEndY(event.getY());
        }
    }
}