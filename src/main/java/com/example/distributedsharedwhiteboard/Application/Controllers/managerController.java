package com.example.distributedsharedwhiteboard.Application.Controllers;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
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
}
