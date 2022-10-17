package com.example.distributedsharedwhiteboard.Application;

import com.example.distributedsharedwhiteboard.Application.Controllers.managerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ContextMenu;

import java.io.IOException;
import java.util.Objects;

public class ManagerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ManagerApplication.class.getResource("hello-view_manager.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(ManagerApplication.class.getResource("styleSheet.css")).toString());
        Image icon = new Image("icon.png");
        stage.getIcons().add(icon);
        stage.setTitle("WhiteBoard - Manager Window");
        stage.setScene(scene);

        // set user cell
        managerController mc = fxmlLoader.getController();
        mc.userList.setCellFactory(lv -> {

            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem kickOutUser = new MenuItem("kick-out");
            kickOutUser.setOnAction(event -> {
                String item = cell.getItem();
                // code to kick user
                System.out.println("kick out " + item);
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

        mc.userList.getItems().add("some test");
        mc.userList.getItems().add("more test");

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
