package com.example.distributedsharedwhiteboard.Application.Controllers;

import com.example.distributedsharedwhiteboard.Application.Models.Manager;
import com.example.distributedsharedwhiteboard.client.CreateWhiteBoard;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.control.*;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public class managerController extends userController {
    private Manager user;

    @Override
    public void initialize(){
        user = (Manager) CreateWhiteBoard.getUser();
        // bind variables
        Bindings.bindContentBidirectional(user.getMsgList(), MsgHistory.getItems());
        user.addMsgItem("test only : message"); // now can access msgHistory via msgList

        Bindings.bindContentBidirectional(user.getUserList(),userList.getItems());
        user.addUserItem("Test only : user1");

        Bindings.bindContentBidirectional(user.getObjectList(),pane.getChildren());
        com.example.distributedsharedwhiteboard.Shape.Circle circle1 = new com.example.distributedsharedwhiteboard.Shape.Circle(1, 1, 1);
        user.addObjectItem(circle1);

        // set user cell
        userList.setCellFactory(lv -> {

            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem kickOutUser = new MenuItem("kick-out");
            kickOutUser.setOnAction(event -> {
                String item = cell.getItem();
                // code to kick user
                if (!item.equals(user.getUserName())){
                    System.out.println("kick out " + item);
                }
            });
            contextMenu.getItems().add(kickOutUser);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell ;
        });

        userList.getItems().add("some test");
        userList.getItems().add("more test");


        //set the default stage
        this.stage = stage;

        // select freehand by default
        drawMode.getToggles().get(0).setSelected(true);

        // set default color as black
        colorPicker.setValue(Color.BLACK);

        // prepare shapes for canvas
        line = new Line();
        circle = new Circle();
        rectangle = new Rectangle();
        path = new Path();
        polygon = new Polygon();
    }
    @FXML
    protected void handleSaveAs(ActionEvent event) {

        FileChooser savefile = new FileChooser();

        savefile.getExtensionFilters().addAll(
                new ExtensionFilter("Image Files", "*.png", "*.jpg"),
                new ExtensionFilter("All Files", "*.*"));
        savefile.setSelectedExtensionFilter(savefile.getExtensionFilters().get(0));
        savefile.setTitle("Save File");
        savefile.setInitialFileName("untitled-" + LocalDateTime.now().getNano());

        File file = savefile.showSaveDialog(stage);
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
                canvas.snapshot(null, writableImage);

                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);

            } catch (IOException ex) {
                System.out.println("Error!");
            }
        }
    }

    // Invoke this message to display a dialog to notify user about unsaved changes
    protected void showUnsaveDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setContentText("There is unsaved changes. Do you want to save them?");

        ButtonType btYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType btNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType btCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btYes, btNo, btCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == btYes) {
            // ... user chose "yes
            handleSaveAs(null);
        } else if (result.get() == btNo) {
            // ... user chose "No"
            handleQuit(null);
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    public void KickOutUser(String user) {
        System.out.println("kick out " + user);
    }
}
